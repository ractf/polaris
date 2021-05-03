package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.instance.Instance;

public interface InstanceDecoratorPlugin extends Plugin {

    Instance decorate(final Instance instance);

}
