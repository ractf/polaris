package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.multibindings.Multibinder;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.node.NodeConfiguration;

public class SchedulerModule extends DropwizardAwareModule<NodeConfiguration> {

    @Override
    protected void configure() {
        final var schedulerBinder = Multibinder.newSetBinder(binder(), OldScheduler.class);
        schedulerBinder.addBinding().to(RoundRobinScheduler.class);
    }
}
