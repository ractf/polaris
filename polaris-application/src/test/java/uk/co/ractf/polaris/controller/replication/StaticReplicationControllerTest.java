package uk.co.ractf.polaris.controller.replication;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.StaticReplication;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StaticReplicationControllerTest {

    @Test
    public void testStaticReplication() {
        final ReplicationController replicationController = new StaticReplicationController(new StaticReplication("static", 15));
        assertThat(replicationController.getScaleAmount(new ArrayList<>(), null)).isEqualTo(15);
    }

}
