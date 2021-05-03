package uk.co.ractf.polaris.state;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.node.Node;

import java.util.List;
import java.util.Map;

@ImplementedBy(ConsulState.class)
public interface ClusterState {

    /**
     * Gets a {@link Map} of host id to {@link Node}
     *
     * @return map of hosts
     */
    @NotNull
    Map<String, NodeInfo> getNodes();

    /**
     * Gets a {@link NodeInfo} by id
     *
     * @param id host id
     * @return the host
     */
    @Nullable
    NodeInfo getNode(final String id);

    /**
     * Sets the info for a node
     *
     * @param nodeInfo node info
     */
    void setNodeInfo(final NodeInfo nodeInfo);

    /**
     * Gets an {@link Instance} by id
     *
     * @param id the instance id
     * @return the instance
     */
    @Nullable
    Instance getInstance(final String id);

    /**
     * Unregisters an {@link Instance} from a given {@link Task}, probably because its been descheduled
     *
     * @param instance the instance
     */
    void deleteInstance(final Instance instance);

    /**
     * Sets the state of an instance
     *
     * @param instance the instance details
     */
    void setInstance(final Instance instance);

    boolean instanceExists(final String id);

    /**
     * Gets a list of {@link Instance}s on a given node. Returns an empty collection if the node id is invalid.
     *
     * @param node node id
     * @return instances on the node
     */
    @NotNull
    Map<String, Instance> getInstancesOnNode(final String node);

    Map<String, Instance> getInstancesOfTask(final TaskId taskId);

    List<String> getInstanceIds();

    Map<String, Instance> getInstances();

    Map<TaskId, Integer> getInstanceCounts();

    Map<TaskId, Task> getTasks();

    List<TaskId> getTaskIds();

    Map<TaskId, Task> getTasks(final String namespace);

    Task getTask(final TaskId id);

    void setTask(final Task task);

    void deleteTask(final TaskId id);

    boolean lockTask(final Task task);

    @CanIgnoreReturnValue
    boolean unlockTask(final Task task);

    Map<String, Namespace> getNamespaces();

    Namespace getNamespace(final String id);

    void setNamespace(final Namespace namespace);

    void deleteNamespace(final String id);

}
