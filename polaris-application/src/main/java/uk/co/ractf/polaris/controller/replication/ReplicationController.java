package uk.co.ractf.polaris.controller.replication;

import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.List;

/**
 * This interface is used to control how a {@link Task} should replicate its {@link Instance}s.
 */
public interface ReplicationController {

    @Contract("null -> new")
    static ReplicationController create(final Replication replication) {
        if (replication instanceof StaticReplication) {
            return new StaticReplicationController((StaticReplication) replication);
        }
        return new NullReplicationController();
    }

    /**
     * Calculate how many instances need scheduling or descheduling based on the current state of the cluster.
     *
     * @param instances    the current instance list
     * @param clusterState the cluster state
     * @return the amount to scale by
     */
    int getScaleAmount(final List<Instance> instances, final ClusterState clusterState);

}
