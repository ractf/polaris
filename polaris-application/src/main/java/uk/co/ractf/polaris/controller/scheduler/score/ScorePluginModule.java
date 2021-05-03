package uk.co.ractf.polaris.controller.scheduler.score;

import com.google.inject.multibindings.Multibinder;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.ScorePlugin;

public class ScorePluginModule extends DropwizardAwareModule<ControllerConfiguration> {

    @Override
    protected void configure() {
        final var multibinder = Multibinder.newSetBinder(binder(), ScorePlugin.class);
        multibinder.addBinding().to(LeastResourceUsage.class);
    }
}
