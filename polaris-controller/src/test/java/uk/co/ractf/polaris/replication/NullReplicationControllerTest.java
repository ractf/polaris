package uk.co.ractf.polaris.replication;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class NullReplicationControllerTest {

    @Test
    public void testNullReplication() {
        assertThat(new NullReplicationController().getScaleAmount(null, null)).isZero();
    }

}
