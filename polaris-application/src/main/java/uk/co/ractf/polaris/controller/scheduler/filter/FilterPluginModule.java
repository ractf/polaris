package uk.co.ractf.polaris.controller.scheduler.filter;

import com.google.inject.multibindings.Multibinder;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.FilterPlugin;

public class FilterPluginModule extends DropwizardAwareModule<ControllerConfiguration> {

    @Override
    protected void configure() {
        final var multibinder = Multibinder.newSetBinder(binder(), FilterPlugin.class);
        multibinder.addBinding().to(NodeHasRunner.class);
        multibinder.addBinding().to(NodeIsSchedulable.class);
    }

}
