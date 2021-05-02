package uk.co.ractf.polaris.controller.scheduler.clusterpredicate;

import com.google.inject.multibindings.Multibinder;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicatePlugin;

public class ClusterPredicatePluginModule extends DropwizardAwareModule<ControllerConfiguration> {

    @Override
    protected void configure() {
        final var multibinder = Multibinder.newSetBinder(binder(), ClusterPredicatePlugin.class);
        multibinder.addBinding().to(RegistryAuth.class);
    }
}
