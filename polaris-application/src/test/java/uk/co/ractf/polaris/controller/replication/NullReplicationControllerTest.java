package uk.co.ractf.polaris.controller.replication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NullReplicationControllerTest {

    @Test
    public void testNullReplication() {
        assertThat(new NullReplicationController().getScaleAmount(null, null)).isZero();
    }

}
