package uk.co.ractf.polaris.controller.task;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DeploymentScaleReconciliationServiceTest {

    private final Scheduler scheduler = mock(Scheduler.class);
    private final Controller controller = mock(Controller.class);
    private final Host host = mock(Host.class);
    private final PolarisConfiguration config = new PolarisConfiguration();
    private Instance instance;

    @BeforeEach
    public void setup() {
        final Container container = new Container("container", "test", "", "", new ArrayList<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(),
                new ResourceQuota(512L, 0L, 1000L), "always", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), 5, new ArrayList<>(), new HashMap<>());
        final Challenge challenge = new Challenge("test", Collections.singletonList(container));
        final Deployment deployment = new Deployment("test", "test", new StaticReplication("static", 15),
                new Allocation("team", 500, 500));
        instance = new Instance("test", "test", "test", "test", new ArrayList<>(), new HashMap<>());
        when(controller.getDeployments()).thenReturn(Map.of("test", deployment));
        when(controller.getHosts()).thenReturn(Map.of("test", host));
        when(controller.getChallengeFromDeployment(deployment)).thenReturn(challenge);
        when(controller.lockDeployment(any())).thenReturn(true);
        when(controller.unlockDeployment(any())).thenReturn(true);
        when(scheduler.scheduleChallenge(any(Challenge.class), anyCollection())).thenReturn(host);
        when(host.createInstance(any(Challenge.class), any(Deployment.class))).thenReturn(instance);
    }

    @Test
    public void testScaleUp() {
        when(controller.getInstancesForDeployment(any())).thenReturn(Collections.emptyList());
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(controller, scheduler, config);
        service.runOneIteration();
        verify(host, times(15)).createInstance(any(Challenge.class), any(Deployment.class));
    }

    @Test
    public void testScaleDown() {
        when(controller.getInstancesForDeployment(any())).thenReturn(Collections.nCopies(20, instance));
        final DeploymentScaleReconciliationService service = new DeploymentScaleReconciliationService(controller, scheduler, config);
        service.runOneIteration();
        verify(controller, times(5)).unregisterInstance(any(), any());
    }

}
