package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskGetCommand extends AbstractCommand<Task> {

    private final TaskId taskId;

    public TaskGetCommand(final APIClientTransport apiClientTransport, final TaskId taskId) {
        super(apiClientTransport);
        this.taskId = taskId;
    }

    public TaskGetCommand(final APIClientTransport apiClientTransport, final String taskId) {
        super(apiClientTransport);
        this.taskId = new TaskId(taskId);
    }

    @Override
    public Task exec() {
        return apiClientTransport.get("/tasks/" + taskId.toString(), Task.class);
    }
}
