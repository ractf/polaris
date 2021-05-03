package uk.co.ractf.polaris.controller.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultTaskScalerTest {

    private ClusterState state;
    private Scheduler scheduler;

    @BeforeEach
    public void setup() {
        state = mock(ClusterState.class);
        scheduler = mock(Scheduler.class);
        when(state.lockTask(any())).thenReturn(true);
        when(state.unlockTask(any())).thenReturn(true);
    }

    @Test
    public void testScaleUp() {
        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verify(scheduler, times(5)).schedule(any());
    }

    @Test
    public void testScaleDown() {
        final var instances = new HashMap<String, Instance>();
        for (var i = 0; i < 10; i++) {
            instances.put(String.valueOf(i), new Instance(null, null, null, null, null));
        }
        when(state.getInstancesOfTask(any())).thenReturn(instances);

        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verify(scheduler, times(5)).deschedule(any());
    }

    @Test
    public void testNoScale() {
        final var instances = new HashMap<String, Instance>();
        for (var i = 0; i < 5; i++) {
            instances.put(String.valueOf(i), new Instance(null, null, null, null, null));
        }
        when(state.getInstancesOfTask(any())).thenReturn(instances);

        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verify(scheduler, never()).deschedule(any());
        verify(scheduler, never()).schedule(any());
    }

    @Test
    public void testLock() {
        when(state.getInstancesOfTask(any())).thenReturn(new HashMap<>());

        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verify(state).lockTask(any());
    }

    @Test
    public void testUnlock() {
        when(state.getInstancesOfTask(any())).thenReturn(new HashMap<>());

        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verify(state).unlockTask(any());
    }

    @Test
    public void testLockFail() {
        when(state.lockTask(any())).thenReturn(false);
        when(state.getInstancesOfTask(any())).thenReturn(new HashMap<>());

        final var scaler = new DefaultTaskScaler(state, scheduler);
        final var task = new Challenge(new TaskId("test:test"), 1, new ArrayList<>(),
                new StaticReplication("", 5), null);
        scaler.scaleTask(task);
        verifyNoInteractions(scheduler);
    }

}
