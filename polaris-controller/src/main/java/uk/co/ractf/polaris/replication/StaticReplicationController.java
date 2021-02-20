package uk.co.ractf.polaris.replication;

import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;

import java.util.List;

public class StaticReplicationController implements ReplicationController {

    private final StaticReplication staticReplication;

    public StaticReplicationController(final StaticReplication staticReplication) {
        this.staticReplication = staticReplication;
    }

    @Override
    public int getScaleAmount(final List<Instance> instances, final Controller controller) {
        return staticReplication.getAmount() - instances.size();
    }

}
