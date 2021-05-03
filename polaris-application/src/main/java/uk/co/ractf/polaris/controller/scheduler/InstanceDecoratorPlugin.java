package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;

public interface InstanceDecoratorPlugin extends Plugin {

    Instance decorate(final Instance instance, final Task task, final NodeInfo nodeInfo);

}
