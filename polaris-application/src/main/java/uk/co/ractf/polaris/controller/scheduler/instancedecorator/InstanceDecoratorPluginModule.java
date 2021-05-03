package uk.co.ractf.polaris.controller.scheduler.instancedecorator;

import com.google.inject.multibindings.Multibinder;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.InstanceDecoratorPlugin;

public class InstanceDecoratorPluginModule extends DropwizardAwareModule<ControllerConfiguration> {

    @Override
    protected void configure() {
        final var multibinder = Multibinder.newSetBinder(binder(), InstanceDecoratorPlugin.class);
        multibinder.addBinding().to(PortBinding.class);
    }
}
