package uk.co.ractf.polaris.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.model.kv.Verb;
import com.orbitz.consul.model.session.ImmutableSession;
import com.orbitz.consul.model.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.util.ConsulPath;

import java.util.*;
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
        final Map<String, NodeInfo> nodeMap = new HashMap<>();
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
        final Map<String, Instance> instances = new HashMap<>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            if (entry.getValue().getNodeId().equals(node)) {
                instances.put(entry.getKey(), entry.getValue());
            }
        }
        return instances;
    }

    @Override
    public Map<String, Instance> getInstancesOfTask(final TaskId taskId) {
        final Map<String, Instance> instances = new HashMap<>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            if (entry.getValue().getTaskId().equals(taskId)) {
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
        final Map<String, Instance> instanceMap = new HashMap<>();
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
    public Map<TaskId, Integer> getInstanceCounts() {
        final Map<TaskId, Integer> instanceCounts = new HashMap<>();
        final var instanceMap = getInstances();
        for (final var entry : instanceMap.entrySet()) {
            final var current = instanceCounts.getOrDefault(entry.getValue().getTaskId(), 0);
            instanceCounts.put(entry.getValue().getTaskId(), current + 1);
        }

        return instanceCounts;
    }

    @Override
    public Map<TaskId, Task> getTasks() {
        final Map<TaskId, Task> taskMap = new HashMap<>();
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
    public List<TaskId> getTaskIds() {
        return consul.keyValueClient().getKeys(ConsulPath.tasks()).stream().map(TaskId::new).collect(Collectors.toList());
    }

    @Override
    public Map<TaskId, Task> getTasks(final String namespace) {
        final Map<TaskId, Task> taskMap = new HashMap<>();
        for (final var taskData : consul.keyValueClient().getValues(ConsulPath.tasks())) {
            if (taskData.getValueAsString().isPresent()) {
                try {
                    final var task = Task.parse(taskData.getValueAsString().get(), Task.class);
                    if (task.getId().getNamespace().equals(namespace)) {
                        taskMap.put(task.getId(), task);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing task " + taskData.getKey(), exception);
                }
            }
        }
        return taskMap;
    }

    @Override
    public Task getTask(final TaskId id) {
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
                        .build());
    }

    @Override
    public void deleteTask(final TaskId id) {
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
}
