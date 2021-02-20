package uk.co.ractf.polaris.replication;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;

import java.util.List;

public class NullReplicationController implements ReplicationController {

    @Override
    public int getScaleAmount(final List<Instance> instances, final Controller controller) {
        return 0;
    }

}
