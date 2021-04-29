package uk.co.ractf.polaris.controller.service;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.node.PortAllocations;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.Scheduler;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DeploymentScaleReconciliationServiceTest {

    private final Scheduler scheduler = mock(Scheduler.class);
    private final Controller controller = mock(Controller.class);
    private final NodeInfo node = mock(NodeInfo.class);
    private final ControllerConfiguration config = new ControllerConfiguration();
    private final ClusterState clusterState = mock(ClusterState.class);
    private Instance instance;

    @BeforeEach
    public void setup() {
        final Container container = new Container("container", "test", "", "", new ArrayList<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(),
                new ResourceQuota(512L, 0L, 1000L), "always", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), 5, new HashSet<>(), new HashMap<>());
        final Challenge challenge = new Challenge("test", Collections.singletonList(container), replication, allocation);
        final Deployment deployment = new Deployment("test", "test", new StaticReplication("static", 15),
                new Allocation("team", 500, 500));
        instance = new Instance("test", "test", "test", "test", new ArrayList<>(), new HashMap<>());

        config.setMinPort(0);
        config.setMaxPort(65535);

        when(clusterState.getDeployments()).thenReturn(Map.of("test", deployment));
        when(clusterState.getNodes()).thenReturn(Map.of("test", node));
        when(clusterState.getChallengeFromDeployment(deployment.getId())).thenReturn(challenge);
        when(clusterState.lockDeployment(any())).thenReturn(true);
        when(clusterState.unlockDeployment(any())).thenReturn(true);
        when(scheduler.scheduleChallenge(any(Challenge.class), anyCollection())).thenReturn(node);
        final PortAllocations portAllocations = PortAllocations.empty();
        when(node.getPortAllocations()).thenReturn(portAllocations);
    }

    @Test
    public void testScaleUp() {
        when(clusterState.getInstancesForDeployment(any())).thenReturn(Collections.emptyList());
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verify(clusterState, times(15)).setInstance(any(Instance.class));
    }

    @Test
    public void testScaleDown() {
        when(clusterState.getInstancesForDeployment(any())).thenReturn(Collections.nCopies(20, instance));
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verify(clusterState, times(5)).deleteInstance(any());
    }

    @Test
    public void testFailedToObtainLock() {
        when(clusterState.lockDeployment(any())).thenReturn(false);
        when(clusterState.unlockDeployment(any())).thenReturn(false);
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verifyNoInteractions(node);
    }

    @Test
    public void testExceptionThrown() {
        when(clusterState.lockDeployment(any())).thenThrow(new RuntimeException());
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(clusterState, scheduler, config);
        service.runOneIteration();
        verifyNoInteractions(node);
    }

}
