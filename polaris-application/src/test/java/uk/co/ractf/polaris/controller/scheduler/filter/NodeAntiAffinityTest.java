package uk.co.ractf.polaris.controller.scheduler.filter;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.task.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeAntiAffinityTest {

    private final NodeAntiAffinity nodeAntiAffinity = new NodeAntiAffinity();

    @Test
    public void testName() {
        assertThat(nodeAntiAffinity.getName()).isEqualTo("NodeAntiAffinity");
    }

    @Test
    public void testAffinityBlankMatch() {
        final var pod = mock(Container.class);
        when(pod.getAffinity()).thenReturn(Map.of("key", ""));
        final var node = mock(NodeInfo.class);
        when(node.getLabels()).thenReturn(Map.of("key", "value"));
        final var task = mock(Task.class);
        when(task.getPods()).thenReturn(Collections.singletonList(pod));

        final var result = nodeAntiAffinity.filter(task, node);
        assertThat(result.isSchedulable()).isTrue();
    }

    @Test
    public void testAffinityValueMatch() {
        final var pod = mock(Container.class);
        when(pod.getAffinity()).thenReturn(Map.of("key", "value"));
        final var node = mock(NodeInfo.class);
        when(node.getLabels()).thenReturn(Map.of("key", "value"));
        final var task = mock(Task.class);
        when(task.getPods()).thenReturn(Collections.singletonList(pod));

        final var result = nodeAntiAffinity.filter(task, node);
        assertThat(result.isSchedulable()).isFalse();
    }

    @Test
    public void testAffinityValueDoesntMatch() {
        final var pod = mock(Container.class);
        when(pod.getAffinity()).thenReturn(Map.of("key", "value2"));
        final var node = mock(NodeInfo.class);
        when(node.getLabels()).thenReturn(Map.of("key", "value"));
        final var task = mock(Task.class);
        when(task.getPods()).thenReturn(Collections.singletonList(pod));

        final var result = nodeAntiAffinity.filter(task, node);
        assertThat(result.isSchedulable()).isTrue();
    }

    @Test
    public void testNoAffinity() {
        final var pod = mock(Container.class);
        when(pod.getAffinity()).thenReturn(new HashMap<>());
        final var node = mock(NodeInfo.class);
        when(node.getLabels()).thenReturn(Map.of("key", "value"));
        final var task = mock(Task.class);
        when(task.getPods()).thenReturn(Collections.singletonList(pod));

        final var result = nodeAntiAffinity.filter(task, node);
        assertThat(result.isSchedulable()).isTrue();
    }

}
