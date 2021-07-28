package uk.co.ractf.polaris.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import com.orbitz.consul.model.session.ImmutableSession;
import com.orbitz.consul.model.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.authentication.APIToken;
import uk.co.ractf.polaris.api.registry.credentials.ContainerRegistryCredentials;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.notification.NotificationReceiver;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.util.ConsulPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class ConsulState implements ClusterState {

    private static final Logger log = LoggerFactory.getLogger(ConsulState.class);

    private final Consul consul;
    private final String sessionId;

    @Inject
    public ConsulState(final Consul consul) {
        this.consul = consul;
        //TODO: this is probably terrible for debugging
        final Session session = ImmutableSession.builder().name(UUID.randomUUID().toString()).build();
        this.sessionId = consul.sessionClient().createSession(session).getId();
    }

    @NotNull
    @Override
    public Map<String, NodeInfo> getNodes() {
        final var nodeMap = new HashMap<String, NodeInfo>();
        for (final var nodeData : consul.keyValueClient().getValues(ConsulPath.nodes())) {
            if (nodeData.getValueAsString().isPresent()) {
                try {
                    final var node = NodeInfo.parse(nodeData.getValueAsString().get(), NodeInfo.class);
                    if (!node.getId().isBlank()) {
                        nodeMap.put(node.getId(), node);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing node " + nodeData.getKey(), exception);
                }
            }
        }
        return nodeMap;
    }

    @Nullable
    @Override
    public NodeInfo getNode(final String id) {
        final var nodeData = consul.keyValueClient().getValueAsString(ConsulPath.node(id));
        if (nodeData.isPresent()) {
            try {
                return NodeInfo.parse(nodeData.get(), NodeInfo.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing node " + id, exception);
            }
        }
        return null;
    }

    @Override
    public void setNodeInfo(final NodeInfo nodeInfo) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.node(nodeInfo.getId()))
                        .value(nodeInfo.toJsonString())
                        .build());
    }

    @Nullable
    @Override
    public Instance getInstance(final String id) {
        final var instanceData = consul.keyValueClient().getValueAsString(ConsulPath.instance(id));
        if (instanceData.isPresent()) {
            try {
                return Instance.parse(instanceData.get(), Instance.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing instance " + id, exception);
            }
        }
        return null;
    }

    @Override
    public void deleteInstance(final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.instance(instance.getId()))
                        .build());
    }

    @Override
    public void setInstance(final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.instance(instance.getId()))
                        .value(instance.toJsonString())
                        .build());
    }

    @Override
    public boolean instanceExists(final String id) {
        return consul.keyValueClient().getValue(ConsulPath.instance(id)).isPresent();
    }

    @NotNull
    @Override
    public Map<String, Instance> getInstancesOnNode(final String node) {
        final var instances = new HashMap<String, Instance>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            if (entry.getValue().getNodeId().equals(node)) {
                instances.put(entry.getKey(), entry.getValue());
            }
        }
        return instances;
    }

    @Override
    public Map<String, Instance> getInstancesOfTask(final NamespacedId namespacedId) {
        final var instances = new HashMap<String, Instance>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            if (entry.getValue().getTaskId().equals(namespacedId)) {
                instances.put(entry.getKey(), entry.getValue());
            }
        }
        return instances;
    }

    @Override
    public List<String> getInstanceIds() {
        return consul.keyValueClient().getKeys(ConsulPath.instances());
    }

    @Override
    public Map<String, Instance> getInstances() {
        final var instanceMap = new HashMap<String, Instance>();
        for (final var instanceData : consul.keyValueClient().getValues(ConsulPath.instances())) {
            if (instanceData.getValueAsString().isPresent()) {
                try {
                    final var deployment = Instance.parse(instanceData.getValueAsString().get(), Instance.class);
                    if (!deployment.getId().isBlank()) {
                        instanceMap.put(deployment.getId(), deployment);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing instance " + instanceData.getKey(), exception);
                }
            }
        }
        return instanceMap;
    }

    @Override
    public Map<NamespacedId, Integer> getInstanceCounts() {
        final var instanceCounts = new HashMap<NamespacedId, Integer>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            final var current = instanceCounts.getOrDefault(entry.getValue().getTaskId(), 0);
            instanceCounts.put(entry.getValue().getTaskId(), current + 1);
        }

        return instanceCounts;
    }

    @Override
    public Map<NamespacedId, Task> getTasks() {
        final var taskMap = new HashMap<NamespacedId, Task>();
        for (final var taskData : consul.keyValueClient().getValues(ConsulPath.tasks())) {
            if (taskData.getValueAsString().isPresent()) {
                try {
                    final var task = Task.parse(taskData.getValueAsString().get(), Task.class);
                    taskMap.put(task.getId(), task);
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing task " + taskData.getKey(), exception);
                }
            }
        }
        return taskMap;
    }

    @Override
    public List<NamespacedId> getTaskIds() {
        return consul.keyValueClient().getKeys(ConsulPath.tasks()).stream().map(NamespacedId::new).collect(Collectors.toList());
    }

    @Override
    public Map<NamespacedId, Task> getTasks(final String namespace) {
        return getTasks().entrySet().stream()
                .filter(e -> e.getKey().getNamespace().equals(namespace))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Task getTask(final NamespacedId id) {
        final var taskData = consul.keyValueClient().getValueAsString(ConsulPath.task(id));
        if (taskData.isPresent()) {
            try {
                return Task.parse(taskData.get(), Task.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing task " + id, exception);
            }
        }
        return null;
    }

    @Override
    public void setTask(final Task task) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.task(task.getId()))
                        .value(task.toJsonString())
                        .build());
    }

    @Override
    public void deleteTask(final NamespacedId id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.task(id))
                        .build());
    }

    @Override
    public boolean lockTask(final Task task) {
        return consul.keyValueClient().acquireLock(ConsulPath.taskLock(task.getId()), sessionId);
    }

    @Override
    public boolean unlockTask(final Task task) {
        return consul.keyValueClient().releaseLock(ConsulPath.taskLock(task.getId()), sessionId);
    }

    @Override
    public Map<String, Namespace> getNamespaces() {
        final var namespaceMap = new HashMap<String, Namespace>();
        for (final var namespaceData : consul.keyValueClient().getValues(ConsulPath.namespaces())) {
            if (namespaceData.getValueAsString().isPresent()) {
                try {
                    final var namespace = Namespace.parse(namespaceData.getValueAsString().get(), Namespace.class);
                    namespaceMap.put(namespace.getName(), namespace);
                } catch (final JsonProcessingException e) {
                    log.error("Error deserializing namespace " + namespaceData.getKey(), e);
                }
            }
        }
        return namespaceMap;
    }

    @Override
    public Namespace getNamespace(final String id) {
        final var namespaceData = consul.keyValueClient().getValueAsString(ConsulPath.namespace(id));
        if (namespaceData.isPresent()) {
            try {
                return Namespace.parse(namespaceData.get(), Namespace.class);
            } catch (final JsonProcessingException e) {
                log.error("Error deserializing namespace " + id, e);
            }
        }
        return null;
    }

    @Override
    public void setNamespace(final Namespace namespace) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.namespace(namespace.getName()))
                        .value(namespace.toJsonString())
                        .build());
    }

    @Override
    public void deleteNamespace(final String id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(id)
                        .build());
    }

    @Override
    public Map<NamespacedId, ContainerRegistryCredentials> getCredentials() {
        final var credentialsMap = new HashMap<NamespacedId, ContainerRegistryCredentials>();
        for (final var credentialData : consul.keyValueClient().getValues(ConsulPath.credentials())) {
            if (credentialData.getValueAsString().isPresent()) {
                try {
                    final var credential = ContainerRegistryCredentials.parse(credentialData.getValueAsString().get(), ContainerRegistryCredentials.class);
                    credentialsMap.put(credential.getId(), credential);
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing credential " + credentialData.getKey(), exception);
                }
            }
        }
        return credentialsMap;
    }

    @Override
    public Map<NamespacedId, ContainerRegistryCredentials> getCredentials(final String namespace) {
        return getCredentials().entrySet().stream()
                .filter(e -> e.getKey().getNamespace().equals(namespace))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public ContainerRegistryCredentials getCredential(final NamespacedId id) {
        final var credentialData = consul.keyValueClient().getValue(ConsulPath.credential(id));
        if (credentialData.isPresent()) {
            if (credentialData.get().getValueAsString().isPresent()) {
                try {
                    return ContainerRegistryCredentials.parse(credentialData.get().getValueAsString().get(), ContainerRegistryCredentials.class);
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing credential " + credentialData.get().getKey(), exception);
                }
            }
        }
        return null;
    }

    @Override
    public void setCredential(final ContainerRegistryCredentials credential) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.credential(credential.getId()))
                        .value(credential.toJsonString())
                        .build());
    }

    @Override
    public void deleteCredential(final NamespacedId id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.credential(id))
                        .build());
    }

    @Override
    public Map<String, APIToken> getAPITokens() {
        final Map<String, APIToken> tokenMap = new HashMap<>();
        for (final var tokenData : consul.keyValueClient().getValues(ConsulPath.tokens())) {
            if (tokenData.getValueAsString().isPresent()) {
                try {
                    final var apiToken = APIToken.parse(tokenData.getValueAsString().get(), APIToken.class);
                    tokenMap.put(apiToken.getId(), apiToken);
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing api token {}", tokenData.getKey(), exception);
                }
            }
        }
        return tokenMap;
    }

    @Override
    public APIToken getAPIToken(final String id) {
        final var tokenData = consul.keyValueClient().getValueAsString(ConsulPath.token(id));
        if (tokenData.isPresent()) {
            try {
                return APIToken.parse(tokenData.get(), APIToken.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing api token {}", id, exception);
            }
        }
        return null;
    }

    @Override
    public void setAPIToken(final APIToken apiToken) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.token(apiToken.getToken()))
                        .value(apiToken.toJsonString())
                        .build());
    }

    @Override
    public void deleteAPIToken(final String id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.token(id))
                        .build());
    }

    public void setNotificationReceiver(final NotificationReceiver receiver) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.notificationReceiver(receiver.getId()))
                        .value(receiver.toJsonString())
                        .build());
    }

    @Override
    public void deleteNotificationReceiver(final NamespacedId id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.notificationReceiver(id))
                        .build());
    }

    @Override
    public Map<NamespacedId, NotificationReceiver> getNotificationReceivers() {
        final var receivers = new HashMap<NamespacedId, NotificationReceiver>();
        for (final var receiverData : consul.keyValueClient().getValues(ConsulPath.notificationReceivers())) {
            if (receiverData.getValueAsString().isPresent()) {
                try {
                    final var receiver = NotificationReceiver.parse(receiverData.getValueAsString().get(), NotificationReceiver.class);
                    receivers.put(receiver.getId(), receiver);
                } catch (final JsonProcessingException e) {
                    log.error("Error deserializing notification receiver " + receiverData.getKey(), e);
                }
            }
        }
        return receivers;
    }

    @Override
    public Map<NamespacedId, NotificationReceiver> getNotificationReceivers(final String namespace) {
        return getNotificationReceivers().entrySet().stream()
                .filter(e -> e.getKey().getNamespace().equals(namespace))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public NotificationReceiver getNotificationReceiver(final NamespacedId id) {
        final var receiverData = consul.keyValueClient().getValue(ConsulPath.notificationReceiver(id));
        if (receiverData.isPresent()) {
            if (receiverData.get().getValueAsString().isPresent()) {
                try {
                    return NotificationReceiver.parse(receiverData.get().getValueAsString().get(), NotificationReceiver.class);
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing notification receiver " + receiverData.get().getKey(), exception);
                }
            }
        }
        return null;
    }
}
