package uk.co.ractf.polaris.controller.scheduler.filter;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.node.NodeInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeIsSchedulableTest {

    private final NodeIsSchedulable nodeIsSchedulable = new NodeIsSchedulable();

    @Test
    public void testName() {
        assertThat(nodeIsSchedulable.getName()).isEqualTo("NodeIsSchedulable");
    }

    @Test
    public void testNodeSchedulable() {
        final var node = mock(NodeInfo.class);
        when(node.isSchedulable()).thenReturn(true);
        assertThat(nodeIsSchedulable.filter(null, node).isSchedulable()).isTrue();
    }

    @Test
    public void testNodeNotSchedulable() {
        final var node = mock(NodeInfo.class);
        when(node.isSchedulable()).thenReturn(false);
        assertThat(nodeIsSchedulable.filter(null, node).isSchedulable()).isFalse();
    }

}
