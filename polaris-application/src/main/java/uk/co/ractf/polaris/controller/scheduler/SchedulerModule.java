package uk.co.ractf.polaris.controller.scheduler;

import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.clusterpredicate.ClusterPredicatePluginModule;
import uk.co.ractf.polaris.controller.scheduler.filter.FilterPluginModule;
import uk.co.ractf.polaris.controller.scheduler.instancedecorator.InstanceDecoratorPluginModule;
import uk.co.ractf.polaris.controller.scheduler.score.ScorePluginModule;

public class SchedulerModule extends DropwizardAwareModule<ControllerConfiguration> {

    @Override
    protected void configure() {
        install(new ClusterPredicatePluginModule());
        install(new FilterPluginModule());
        install(new ScorePluginModule());
        install(new InstanceDecoratorPluginModule());
    }
}
