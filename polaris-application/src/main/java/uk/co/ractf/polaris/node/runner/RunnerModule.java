package uk.co.ractf.polaris.node.runner;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class RunnerModule extends AbstractModule {
    @Override
    protected void configure() {
        final Multibinder<Runner<?>> runnerBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<>() {});
        runnerBinder.addBinding().to(DockerRunner.class);
    }
}
