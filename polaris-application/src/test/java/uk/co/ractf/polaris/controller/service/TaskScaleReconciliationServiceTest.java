package uk.co.ractf.polaris.controller.service;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.node.PortAllocations;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.Scheduler;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TaskScaleReconciliationServiceTest {

    private final Scheduler scheduler = mock(Scheduler.class);
    private final Controller controller = mock(Controller.class);
    private final NodeInfo node = mock(NodeInfo.class);
    private final ControllerConfiguration config = new ControllerConfiguration();
    private final ClusterState clusterState = mock(ClusterState.class);
    private Instance instance;

    @BeforeEach
    public void setup() {
        final var container = new Container("container", "test", "", "", new ArrayList<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(),
                new ResourceQuota(512L, 0L, 1000L), "always", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), 5, new ArrayList<>(), new HashMap<>());

        final var taskId = new TaskId("test", "test");
        final var task = new Challenge(taskId, 0, Collections.singletonList(container),
                new StaticReplication("static", 15), new Allocation("team", 500, 500));
        instance = new Instance("test", taskId, "test", new ArrayList<>(), new HashMap<>());

        config.setMinPort(0);
        config.setMaxPort(65535);

        when(clusterState.getTasks()).thenReturn(Map.of(taskId, task));
        when(clusterState.getNodes()).thenReturn(Map.of("test", node));
        when(clusterState.lockTask(any())).thenReturn(true);
        when(clusterState.unlockTask(any())).thenReturn(true);
        when(scheduler.scheduleTask(any(Task.class), anyCollection())).thenReturn(node);

        final var portAllocations = PortAllocations.empty();
        when(node.getPortAllocations()).thenReturn(portAllocations);
    }

    @Test
    public void testScaleUp() {
        when(clusterState.getInstancesOfTask(any())).thenReturn(new HashMap<>());
        final var service = new TaskScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verify(clusterState, times(15)).setInstance(any(Instance.class));
    }

    @Test
    public void testScaleDown() {
        final var instances = new HashMap<String, Instance>();
        for (var i = 0; i < 20; i++) {
            instances.put(String.valueOf(i), instance);
        }
        when(clusterState.getInstancesOfTask(any())).thenReturn(instances);

        final var service = new TaskScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verify(clusterState, times(5)).deleteInstance(any());
    }

    @Test
    public void testFailedToObtainLock() {
        when(clusterState.lockTask(any())).thenReturn(false);
        when(clusterState.unlockTask(any())).thenReturn(false);
        final var service = new TaskScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verifyNoInteractions(node);
    }

    @Test
    public void testExceptionThrown() {
        when(clusterState.lockTask(any())).thenThrow(new RuntimeException());
        final var service = new TaskScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verifyNoInteractions(node);
    }

}
