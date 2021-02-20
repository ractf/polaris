package uk.co.ractf.polaris.replication;

import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;

import java.util.List;

public interface ReplicationController {

    int getScaleAmount(final List<Instance> instances, final Controller controller);

    static ReplicationController create(final Replication replication) {
        if (replication instanceof StaticReplication) {
            return new StaticReplicationController((StaticReplication) replication);
        }
        return new NullReplicationController();
    }

}
