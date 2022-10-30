package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.task.TaskDeleteResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskDeleteCommand extends AbstractCommand<TaskDeleteResponse> {

    private final NamespacedId namespacedId;

    public TaskDeleteCommand(final APIClientTransport apiClientTransport, final NamespacedId namespacedId) {
        super(apiClientTransport);
        this.namespacedId = namespacedId;
    }

    public TaskDeleteCommand(final APIClientTransport apiClientTransport, final String taskId) {
        super(apiClientTransport);
        this.namespacedId = new NamespacedId(taskId);
    }

    @Override
    public TaskDeleteResponse exec() {
        return apiClientTransport.delete("/tasks/" + namespacedId.toString(), TaskDeleteResponse.class);
    }
}
