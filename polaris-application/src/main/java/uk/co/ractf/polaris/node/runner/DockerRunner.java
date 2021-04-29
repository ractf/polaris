package uk.co.ractf.polaris.node.runner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * An implementation of {@link Runner} that is capable of running docker {@link Container}s through the docker-java {@link DockerClient}
 */
@Singleton
public class DockerRunner implements Runner<Container> {

    private static final Logger log = LoggerFactory.getLogger(DockerRunner.class);

    private final DockerClient dockerClient;
    private final Node node;
    private final ClusterState state;

    private final Set<String> images = new ConcurrentSkipListSet<>();
    private final Set<String> downloadingImages = new ConcurrentSkipListSet<>();
    private final Set<String> dyingContainers = new ConcurrentSkipListSet<>();
    private final Set<String> startingContainers = new ConcurrentSkipListSet<>();

    @Inject
    public DockerRunner(final DockerClient dockerClient, final Node node, final ClusterState state) {
        this.dockerClient = dockerClient;
        this.node = node;
        this.state = state;
    }

    private Capability[] createCapabilityArray(final List<String> capabilities) {
        final List<Capability> list = new ArrayList<>();
        for (final String cap : capabilities) {
            list.add(Capability.valueOf(cap));
        }
        return list.toArray(new Capability[0]);
    }

    @Override
    public void startPod(final Container container, final Instance instance) {
        if (startingContainers.contains(container.getId() + instance.getId())) {
            return;
        }
        try {
            log.info("starting {}", instance.getId());
            startingContainers.add(container.getId() + instance.getId());
            final Map<String, String> labels = container.getLabels();
            labels.put("polaris", container.getId());
            labels.put("polaris-instance", instance.getId());
            labels.put("polaris-deployment", instance.getDeploymentId());
            labels.put("polaris-challenge", instance.getChallengeId());
            labels.put("polaris-pod", container.getId());

            final List<InstancePortBinding> instancePortBindings = instance.getPortBindings();
            final List<PortBinding> portBindings = new ArrayList<>();
            for (final InstancePortBinding instancePortBinding : instancePortBindings) {
                portBindings.add(new PortBinding(new Ports.Binding("0.0.0.0", instancePortBinding.getInternalPort()),
                        ExposedPort.parse(instancePortBinding.getPort())));
            }

            CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(container.getImage());
            createContainerCmd = createContainerCmd
                    .withHostName(container.getId() + "-" + instance.getDeploymentId() + "-" + instance.getId().split("-")[0])
                    .withEnv(container.getFullEnv())
                    .withLabels(labels)
                    .withPortSpecs()
                    .withHostConfig(
                            HostConfig.newHostConfig()
                                    .withPortBindings(portBindings)
                                    .withCapAdd(createCapabilityArray(container.getCapAdd()))
                                    .withCapDrop(createCapabilityArray(container.getCapDrop()))
                                    .withNanoCPUs(container.getResourceQuota().getNanocpu())
                                    .withMemory(container.getResourceQuota().getMemory())
                                    .withRestartPolicy(RestartPolicy.alwaysRestart())
                                    .withMemorySwap(container.getResourceQuota().getSwap()));

            if (container.getEntrypoint().size() > 0) {
                createContainerCmd = createContainerCmd.withCmd(container.getEntrypoint());
            }

            final CreateContainerResponse createContainerResponse = createContainerCmd.exec();
            final StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(createContainerResponse.getId());
            startContainerCmd.exec();
            startingContainers.remove(container.getId() + instance.getId());

            instance.getRandomEnv().putAll(container.getGeneratedRandomEnv());
            log.info("Updating instance {} {}", instance.getId(), instance.toJsonString());
            state.setInstance(instance);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPod(final Container pod, final Instance instance) {
        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        filter.put("polaris-instance", instance.getId());
        for (final com.github.dockerjava.api.model.Container container : dockerClient.listContainersCmd().withLabelFilter(filter).exec()) {
            dockerClient.stopContainerCmd(container.getId()).withTimeout(pod.getTerminationTimeout()).exec();
        }
    }

    @Override
    public void forceUpdatePod(final Container pod, final Instance instance) {
        stopPod(pod, instance);
        preparePod(pod);
        startPod(pod, instance);
    }

    @Override
    public void restartPod(final Container pod, final Instance instance) {
        final Map<String, String> filter = new HashMap<>();
        filter.put("polaris-pod", pod.getId());
        filter.put("polaris-instance", instance.getId());
        for (final com.github.dockerjava.api.model.Container container : dockerClient.listContainersCmd().withLabelFilter(filter).exec()) {
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
        return dockerClient.listContainersCmd().withLabelFilter(filter).withShowAll(true).exec().size() > 0;
    }

    @Override
    public void preparePod(final Container pod) {
        if (downloadingImages.contains(pod.getImage())) {
            return;
        }
        try {
            downloadingImages.add(pod.getImage());
            String tag = "latest";
            if (pod.getImage().replaceAll("http(s?):", "").contains(":")) {
                tag = pod.getImage().split(":")[1];
            }
            dockerClient.pullImageCmd(pod.getImage()).withTag(tag).withAuthConfig(node.getAuthConfig()).exec(new PullImageResultCallback()).awaitCompletion();
            images.add(pod.getImage());
        } catch (final InterruptedException exception) {
            log.error("Error pulling image", exception);
        }
        downloadingImages.remove(pod.getImage());
    }

    @Override
    public void garbageCollect() {
        final List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd().withShowAll(Boolean.TRUE).exec();
        for (final com.github.dockerjava.api.model.Container container : containers) {
            if ("exited".equals(container.getState()) && container.getLabels().containsKey("polaris")) {
                dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(Boolean.TRUE).withForce(Boolean.TRUE).exec();
                log.info("Garbage collected container: " + container.getId());
            }
        }
    }

    @Override
    public void killOrphans() {
        for (final com.github.dockerjava.api.model.Container container :
                dockerClient.listContainersCmd().withLabelFilter(Collections.singletonList("polaris")).exec()) {
            final String podId = container.getLabels().get("polaris");
            final String instanceId = container.getLabels().get("polaris-instance");
            final String challengeId = container.getLabels().get("polaris-challenge");

            if (!state.getInstancesOnNode(node.getId()).containsKey(instanceId)) {
                final Challenge challenge = state.getChallenge(challengeId);
                if (dyingContainers.contains(container.getId())) {
                    continue;
                }

                dyingContainers.add(container.getId());
                CompletableFuture.runAsync(() -> {
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

}
