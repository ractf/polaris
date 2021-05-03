package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.apiclient.command.*;

public interface APIClient {

    static APIClient create(final String apiRoot, final String username, final String password) {
        return new PolarisAPIClient(apiRoot, username, password);
    }

    TaskCreateCommand createTask(final Task task);

    TaskUpdateCommand updateTask(final Task task);

    TaskListCommand listTasks();

    TaskGetCommand getTask(final TaskId taskId);

    TaskGetCommand getTask(final String taskId);

    TaskDeleteCommand deleteTask(final TaskId taskId);

    TaskDeleteCommand deleteTask(final String taskId);

    NamespaceCreateCommand createNamespace(final Namespace namespace);

    NamespaceUpdateCommand updateNamespace(final Namespace namespace);

    NamespaceListCommand listNamespaces();

    NamespaceGetCommand getNamespace(final String id);

    NamespaceDeleteCommand deleteNamespace(final String id);

}
