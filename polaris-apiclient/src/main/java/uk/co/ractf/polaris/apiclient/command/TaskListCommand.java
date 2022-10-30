package uk.co.ractf.polaris.apiclient.command;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

import java.util.Map;
import java.util.StringJoiner;

public class TaskListCommand extends AbstractCommand<Map<NamespacedId, Task>> {

    private String namespace;
    private String filter;
    private String type;

    public TaskListCommand(final APIClientTransport apiClientTransport) {
        super(apiClientTransport);
    }

    public TaskListCommand inNamespace(final String namespace) {
        this.namespace = namespace;
        return this;
    }

    public TaskListCommand filter(final String filter) {
        this.filter = filter;
        return this;
    }

    public TaskListCommand ofType(final String type) {
        this.type = type;
        return this;
    }

    @Override
    public Map<NamespacedId, Task> exec() {
        final var joiner = new StringJoiner("&");
        if (namespace != null) {
            joiner.add("namespace=" + namespace);
        }
        if (filter != null) {
            joiner.add("filter=" + filter);
        }
        if (type != null) {
            joiner.add("type=" + type);
        }

        if (joiner.length() > 0) {
            return apiClientTransport.get("/tasks?" + joiner.toString(), new TypeReference<>() {
            });
        } else {
            return apiClientTransport.get("/tasks", new TypeReference<>() {
            });
        }
    }

}
