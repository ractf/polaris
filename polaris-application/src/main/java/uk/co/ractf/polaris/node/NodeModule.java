package uk.co.ractf.polaris.node;

import com.google.inject.AbstractModule;
import uk.co.ractf.polaris.node.runner.RunnerModule;
import uk.co.ractf.polaris.node.service.NodeServiceModule;

public class NodeModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new NodeServiceModule());
        install(new RunnerModule());
    }
}
