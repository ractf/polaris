package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskSubmitResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskCreateCommand extends AbstractCommand<TaskSubmitResponse> {

    private final Task task;

    public TaskCreateCommand(final APIClientTransport apiClientTransport, final Task task) {
        super(apiClientTransport);
        this.task = task;
    }

    @Override
    public TaskSubmitResponse exec() {
        return apiClientTransport.post("/tasks", task, TaskSubmitResponse.class);
    }
}
