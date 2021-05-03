package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.TaskDeleteResponse;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskDeleteCommand extends AbstractCommand<TaskDeleteResponse> {

    private final TaskId taskId;

    public TaskDeleteCommand(final APIClientTransport apiClientTransport, final TaskId taskId) {
        super(apiClientTransport);
        this.taskId = taskId;
    }

    public TaskDeleteCommand(final APIClientTransport apiClientTransport, final String taskId) {
        super(apiClientTransport);
        this.taskId = new TaskId(taskId);
    }

    @Override
    public TaskDeleteResponse exec() {
        return apiClientTransport.delete("/tasks/" + taskId.toString(), TaskDeleteResponse.class);
    }
}
