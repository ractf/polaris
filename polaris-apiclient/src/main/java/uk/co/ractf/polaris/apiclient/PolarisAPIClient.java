package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.apiclient.command.*;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;
import uk.co.ractf.polaris.apiclient.transport.HttpClientTransport;

public class PolarisAPIClient implements APIClient {

    private final APIClientTransport transport;

    PolarisAPIClient(final String apiRoot, final String username, final String password) {
        this.transport = new HttpClientTransport(apiRoot, username, password);
    }

    @Override
    public PingCommand ping() {
        return new PingCommand(transport);
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
    public TaskGetCommand getTask(final NamespacedId namespacedId) {
        return new TaskGetCommand(transport, namespacedId);
    }

    @Override
    public TaskGetCommand getTask(final String taskId) {
        return new TaskGetCommand(transport, taskId);
    }

    @Override
    public TaskDeleteCommand deleteTask(final NamespacedId namespacedId) {
        return new TaskDeleteCommand(transport, namespacedId);
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
    public NamespaceUpdateCommand updateNamespace(final Namespace namespace) {
        return new NamespaceUpdateCommand(transport, namespace);
    }

    @Override
    public NamespaceListCommand listNamespaces() {
        return new NamespaceListCommand(transport);
    }

    @Override
    public NamespaceGetCommand getNamespace(final String id) {
        return new NamespaceGetCommand(transport, id);
    }

    @Override
    public NamespaceDeleteCommand deleteNamespace(final String id) {
        return new NamespaceDeleteCommand(transport, id);
    }
}
