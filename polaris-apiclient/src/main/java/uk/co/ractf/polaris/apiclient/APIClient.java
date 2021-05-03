package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.apiclient.command.*;

public interface APIClient {

    static APIClient create(final String apiRoot, final String username, final String password) {
        return new PolarisAPIClient(apiRoot, username, password);
    }

    TaskCreateCommand createTask(final Task task);

    TaskUpdateCommand updateTask(final Task task);

}
