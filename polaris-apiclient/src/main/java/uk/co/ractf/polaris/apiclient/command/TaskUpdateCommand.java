package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskUpdateResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class TaskUpdateCommand extends AbstractCommand<TaskUpdateResponse> {

    private final Task task;

    public TaskUpdateCommand(final APIClientTransport apiClientTransport, final Task task) {
        super(apiClientTransport);
        this.task = task;
    }

    @Override
    public TaskUpdateResponse exec() {
        return apiClientTransport.post("/tasks/" + task.getId(), task, TaskUpdateResponse.class);
    }
}
