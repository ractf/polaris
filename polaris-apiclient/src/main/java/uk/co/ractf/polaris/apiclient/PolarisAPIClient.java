package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.apiclient.command.*;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;
import uk.co.ractf.polaris.apiclient.transport.HttpClientTransport;

public class PolarisAPIClient implements APIClient {

    private final APIClientTransport transport;

    PolarisAPIClient(final String apiRoot, final String username, final String password) {
        this.transport = new HttpClientTransport(apiRoot, username, password);
    }

    @Override
    public TaskCreateCommand createTask(final Task task) {
        return new TaskCreateCommand(transport, task);
    }

    @Override
    public TaskUpdateCommand updateTask(final Task task) {
        return new TaskUpdateCommand(transport, task);
    }

    @Override
    public TaskListCommand listTasks() {
        return new TaskListCommand(transport);
    }

    @Override
    public TaskGetCommand getTask(final TaskId taskId) {
        return new TaskGetCommand(transport, taskId);
    }

    @Override
    public TaskGetCommand getTask(final String taskId) {
        return new TaskGetCommand(transport, taskId);
    }

    @Override
    public TaskDeleteCommand deleteTask(final TaskId taskId) {
        return new TaskDeleteCommand(transport, taskId);
    }

    @Override
    public TaskDeleteCommand deleteTask(final String taskId) {
        return new TaskDeleteCommand(transport, taskId);
    }

    @Override
    public NamespaceCreateCommand createNamespace(final Namespace namespace) {
        return new NamespaceCreateCommand(transport, namespace);
    }

    @Override
    public NamespaceListCommand listNamespaces() {
        return new NamespaceListCommand(transport);
    }
}
