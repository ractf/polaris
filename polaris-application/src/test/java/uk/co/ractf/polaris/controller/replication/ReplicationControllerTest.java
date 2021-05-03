package uk.co.ractf.polaris.controller.replication;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.deployment.StaticReplication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ReplicationControllerTest {

    @Test
    public void testStaticReplication() {
        assertThat(ReplicationController.create(new StaticReplication("static", 15))).isInstanceOf(StaticReplicationController.class);
    }

    @Test
    public void testNullReplication() {
        assertThat(ReplicationController.create(new Replication("") {
            @Override
            public String getType() {
                return "";
            }
        })).isInstanceOf(NullReplicationController.class);
    }

}
