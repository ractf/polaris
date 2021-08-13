package uk.co.ractf.polaris.node.runner.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.notification.NotificationTarget;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.notification.NotificationFacade;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Runner} that is capable of running docker {@link Container}s through the docker-java {@link DockerClient}
 */
@Singleton
public class DockerRunner implements Runner<Container> {

    private static final Logger log = LoggerFactory.getLogger(DockerRunner.class);

    private final DockerClient dockerClient;
    private final Node node;
    private final ClusterState state;
    private final AuthConfigFactory authConfigFactory;
    private final NotificationFacade notifications;

    private final Set<String> images = new ConcurrentSkipListSet<>();
    private final Set<String> downloadingImages = new ConcurrentSkipListSet<>();
    private final Set<String> dyingContainers = new ConcurrentSkipListSet<>();
    private final Set<String> startingContainers = new ConcurrentSkipListSet<>();
    private final Map<String, String> instanceContainerIds = new HashMap<>();
    private final Map<String, String> instanceNetworkIds = new HashMap<>();

    @Inject
    public DockerRunner(final DockerClient dockerClient, final Node node, final ClusterState state,
                        final AuthConfigFactory authConfigFactory, final NotificationFacade notifications) {
        this.dockerClient = dockerClient;
        this.node = node;
        this.state = state;
        this.authConfigFactory = authConfigFactory;
        this.notifications = notifications;
    }

    private Capability[] createCapabilityArray(final List<String> capabilities) {
        if (capabilities == null) {
            return new Capability[0];
        }
        final List<Capability> list = new ArrayList<>();
        for (final var cap : capabilities) {
            list.add(Capability.valueOf(cap));
        }
        return list.toArray(new Capability[0]);
    }

    @Override
    public void createPod(final Task task, final Container container, final Instance instance) {
        var labels = container.getLabels();
        if (labels == null) {
            labels = new HashMap<>();
        }
        labels.put("polaris", container.getId());
        labels.put("polaris-instance", instance.getId());
        labels.put("polaris-task", instance.getTaskId().toString());
        labels.put("polaris-pod", container.getId());

        final var instancePortBindings = instance.getPortBindings();
        final List<PortBinding> portBindings = new ArrayList<>();
        for (final var instancePortBinding : instancePortBindings) {
            portBindings.add(new PortBinding(new Ports.Binding("", instancePortBinding.getPort()),
                    ExposedPort.parse(instancePortBinding.getInternalPort())));
        }

        var createContainerCmd = dockerClient.createContainerCmd(container.getImage());
        createContainerCmd = createContainerCmd
                .withHostName(container.getId().split("-")[0] + "-" + instance.getTaskId().toString().split("-")[0] + "-" + instance.getId().split("-")[0])
                .withEnv(container.getFullEnv())
                .withLabels(labels)
                .withExposedPorts(instance.getPortBindings().stream().map(x -> ExposedPort.parse(x.getInternalPort())).collect(Collectors.toList()))
                .withHostConfig(
                        HostConfig.newHostConfig()
                                .withPortBindings(portBindings)
                                .withCapAdd(createCapabilityArray(container.getCapAdd()))
                                .withCapDrop(createCapabilityArray(container.getCapDrop()))
                                .withNanoCPUs(container.getResourceQuota().getNanocpu())
                                .withMemory(container.getResourceQuota().getMemory())
                                .withRestartPolicy(RestartPolicy.alwaysRestart())
                                .withMemorySwap(container.getResourceQuota().getSwap()));

        if (container.getEntrypoint() != null && container.getEntrypoint().size() > 0) {
            createContainerCmd = createContainerCmd.withCmd(container.getEntrypoint());
        }

        instanceContainerIds.put(instance.getId(), createContainerCmd.exec().getId());
    }

    @Override
    public void startPod(final Task task, final Container container, final Instance instance) {
        if (startingContainers.contains(container.getId() + instance.getId())) {
            return;
        }
        try {
            log.info("starting {}", instance.getId());
            startingContainers.add(container.getId() + instance.getId());

            if (instanceNetworkIds.get(instance.getId()) != null) {
                dockerClient.connectToNetworkCmd()
                        .withContainerId(instanceContainerIds.get(instance.getId()))
                        .withNetworkId(instanceNetworkIds.get(instance.getId()))
                        .withContainerNetwork(new ContainerNetwork().withAliases(container.getId()))
                        .exec();
            }
            final var startContainerCmd = dockerClient.startContainerCmd(instanceContainerIds.get(instance.getId()));
            startContainerCmd.exec();
            startingContainers.remove(container.getId() + instance.getId());

            instance.getRandomEnv().putAll(container.getGeneratedRandomEnv());
            log.info("Updating instance {} {}", instance.getId(), instance.toJsonString());
            state.setInstance(instance);
        } catch (final Exception e) {
            e.printStackTrace();
            notifications.error(NotificationTarget.NAMESPACE_ADMIN, state.getNamespace(task.getId().getNamespace()),
                    "Failed to start container " + container.getId() + "(Task: " + task.getId().toString() + ")",
                    e.getMessage() + "(Instance: " + instance.getId() + ")");
        }
    }

    @Override
    public void stopPod(final Container pod, final Instance instance) {
        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        filter.put("polaris-instance", instance.getId());
        for (final var container : dockerClient.listContainersCmd().withLabelFilter(filter).exec()) {
            dockerClient.stopContainerCmd(container.getId()).withTimeout(pod.getTerminationTimeout()).exec();
        }
    }

    @Override
    public void forceUpdatePod(final Task task, final Container pod, final Instance instance) {
        stopPod(pod, instance);
        preparePod(task, pod);
        startPod(task, pod, instance);
    }

    @Override
    public void updatePod(final Task task, final Container pod) {
        final var credentials = state.getCredential(pod.getRepoCredentials());
        final var authConfig = authConfigFactory.createAuthConfig(credentials);

        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        //TODO: surely there's a better way to do this
        try {
            log.info("Trying to pull image {}", pod.getImage());
            dockerClient.pullImageCmd(pod.getImage()).withTag(pod.getTag()).withAuthConfig(authConfig).exec(new PullImageResultCallback() {
                @Override
                public void onNext(final PullResponseItem item) {
                    System.out.println(item.toString());
                    if (item.getStatus() != null && item.getStatus().contains("Downloaded newer image")) {
                        for (final var container : dockerClient.listContainersCmd().withLabelFilter(filter).exec()) {
                            dockerClient.stopContainerCmd(container.getId()).withTimeout(pod.getTerminationTimeout()).exec();
                            createPod(task, pod, state.getInstance(container.getLabels().get("polaris-instance")));
                            startPod(task, pod, state.getInstance(container.getLabels().get("polaris-instance")));
                        }
                        Namespace namespace = null;
                        if (pod.getRepoCredentials() != null) {
                            namespace = state.getNamespace(pod.getRepoCredentials().getNamespace());
                        }
                        notifications.info(NotificationTarget.NAMESPACE_ADMIN, namespace, "Updated pod " + pod.getId(), "");
                    }
                }
            }).awaitCompletion();
        } catch (final Exception e) {
            Namespace namespace = null;
            if (pod.getRepoCredentials() != null) {
                namespace = state.getNamespace(pod.getRepoCredentials().getNamespace());
            }
            notifications.error(NotificationTarget.NAMESPACE_ADMIN, namespace, "Failed to update pod " + pod.getId(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void restartPod(final Container pod, final Instance instance) {
        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        filter.put("polaris-instance", instance.getId());
        for (final var container : dockerClient.listContainersCmd().withLabelFilter(filter).exec()) {
            dockerClient.restartContainerCmd(container.getId()).withtTimeout(pod.getTerminationTimeout()).exec();
        }
    }

    @Override
    public boolean canStartPod(final Container pod) {
        return images.contains(pod.getImage());
    }

    @Override
    public boolean isPodStarted(final Container pod, final Instance instance) {
        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        filter.put("polaris-instance", instance.getId());
        return dockerClient.listContainersCmd().withLabelFilter(filter).withStatusFilter(Collections.singletonList("running")).withShowAll(true).exec().size() > 0;
    }

    @Override
    public void preparePod(final Task task, final Container pod) {
        if (downloadingImages.contains(pod.getImage())) {
            return;
        }
        try {
            downloadingImages.add(pod.getImage());
            var tag = "latest";
            if (pod.getImage().replaceAll("http(s?):", "").contains(":")) {
                tag = pod.getImage().split(":")[1];
            }

            final var credentials = state.getCredential(pod.getRepoCredentials());
            final var authConfig = authConfigFactory.createAuthConfig(credentials);

            dockerClient.pullImageCmd(pod.getImage())
                    .withTag(tag)
                    .withAuthConfig(authConfig)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();

            images.add(pod.getImage());
        } catch (final Exception exception) {
            log.error("Error pulling image", exception);
            notifications.error(NotificationTarget.NAMESPACE_ADMIN, state.getNamespace(task.getId().getNamespace()),
                    "Failed to pull image for " + pod.getId(),
                    "Image: " + pod.getImage() + "\nException: " + exception.getMessage());
        }
        downloadingImages.remove(pod.getImage());
    }

    @Override
    public void garbageCollect() {
        final var containers = dockerClient.listContainersCmd().withShowAll(Boolean.TRUE).exec();
        for (final var container : containers) {
            if ("exited".equals(container.getState()) && container.getLabels().containsKey("polaris")) {
                dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(Boolean.TRUE).withForce(Boolean.TRUE).exec();
                log.info("Garbage collected container: " + container.getId());
            }
        }
    }

    @Override
    public void killOrphans() {
        System.out.println("DockerRunner.killOrphans");
        final var instances = state.getInstancesOnNode(node.getId());
        for (final var container :
                dockerClient.listContainersCmd().withLabelFilter(Collections.singletonList("polaris")).exec()) {
            System.out.println(container.getId());
            final var podId = container.getLabels().get("polaris");
            final var instanceId = container.getLabels().get("polaris-instance");
            final var taskId = container.getLabels().get("polaris-task");

            if (!instances.containsKey(instanceId)) {
                final var challenge = state.getTask(new NamespacedId(taskId));
                if (dyingContainers.contains(container.getId())) {
                    continue;
                }

                dyingContainers.add(container.getId());
                CompletableFuture.runAsync(() -> {
                    log.info("Killing orphaned container {}", instanceId);
                    if (challenge != null) {
                        final int terminationTimeout = ((Container) challenge.getPod(podId)).getTerminationTimeout();
                        dockerClient.stopContainerCmd(container.getId()).withTimeout(terminationTimeout).exec();
                    } else {
                        dockerClient.stopContainerCmd(container.getId()).withTimeout(1).exec();
                    }
                    dyingContainers.remove(container.getId());
                });
            }
        }
    }

    @Override
    public Class<Container> getType() {
        return Container.class;
    }

    @Override
    public String getName() {
        return "docker";
    }

    @Override
    public List<String> getImages() {
        return dockerClient.listImagesCmd().exec().stream().map(Image::getId).collect(Collectors.toList());
    }

    @Override
    public void createNetwork(final List<Container> pods, final Task task, final Instance instance) {
        instanceNetworkIds.put(instance.getId(),
                dockerClient.createNetworkCmd().withName(instance.getId() + "-" + task.getId()).exec().getId());
    }

}
