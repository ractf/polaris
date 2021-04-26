package uk.co.ractf.polaris.node.runner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Capability;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.RestartPolicy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.consul.ConsulPath;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.node.Node;

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
    private final Controller controller;
    private final Node node;
    private final Consul consul;

    private final Set<String> images = new ConcurrentSkipListSet<>();
    private final Set<String> downloadingImages = new ConcurrentSkipListSet<>();
    private final Set<String> dyingContainers = new ConcurrentSkipListSet<>();
    private final Set<String> startingContainers = new ConcurrentSkipListSet<>();

    @Inject
    public DockerRunner(final DockerClient dockerClient, final Controller controller, final Node node,
                        final Consul consul) {
        this.dockerClient = dockerClient;
        this.controller = controller;
        this.node = node;
        this.consul = consul;
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
            labels.put("polaris-challenge", controller.getChallengeFromDeployment(instance.getDeploymentId()).getId());
            labels.put("polaris-pod", container.getId());

            final Map<PortMapping, PortBinding> portBindings = node.createPortBindings(container.getPortMappings());
            for (Map.Entry<PortMapping, PortBinding> portShit : portBindings.entrySet()) {
                System.out.println(portShit.getKey());
                System.out.println(portShit.getValue());
            }

            CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(container.getImage());
            createContainerCmd = createContainerCmd
                    .withHostName(container.getId() + "-" + instance.getDeploymentId() + "-" + instance.getId().split("-")[0])
                    .withEnv(container.getFullEnv())
                    .withLabels(labels)
                    .withPortSpecs()
                    .withHostConfig(
                            HostConfig.newHostConfig()
                                    .withPortBindings(new ArrayList<>(portBindings.values()))
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
            for (final Map.Entry<PortMapping, PortBinding> entry : portBindings.entrySet()) {
                instance.addPortBinding(new InstancePortBinding(entry.getValue().getBinding().getHostPortSpec(),
                        node.getHostInfo().getPublicIP(), entry.getKey().isAdvertise()));
            }
            log.info("Updating instance {} {}", instance.getId(), instance.toJsonString());
            consul.keyValueClient().performTransaction(
                    Operation.builder(Verb.SET)
                            .key(ConsulPath.instance(instance.getId()))
                            .value(instance.toJsonString())
                            .build());
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

            if (!node.getInstances().containsKey(instanceId)) {
                final Challenge challenge = controller.getChallenge(challengeId);
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
