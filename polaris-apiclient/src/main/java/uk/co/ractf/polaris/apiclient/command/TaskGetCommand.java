package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskGetCommand extends AbstractCommand<Task> {

    private final NamespacedId namespacedId;

    public TaskGetCommand(final APIClientTransport apiClientTransport, final NamespacedId namespacedId) {
        super(apiClientTransport);
        this.namespacedId = namespacedId;
    }

    public TaskGetCommand(final APIClientTransport apiClientTransport, final String taskId) {
        super(apiClientTransport);
        this.namespacedId = new NamespacedId(taskId);
    }

    @Override
    public Task exec() {
        return apiClientTransport.get("/tasks/" + namespacedId.toString(), Task.class);
    }
}
