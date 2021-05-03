package uk.co.ractf.polaris.cli.subcommands.task;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@CommandLine.Command(name = "create", aliases = {"submit"})
public class TaskCreate extends Subcommand {

    @CommandLine.Parameters
    private List<File> files;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        for (final var file : files) {
            final var task = Task.parse(Files.readString(file.toPath()), Task.class);
            final var response = apiClient.createTask(task).exec();
            switch (response.getStatus()) {
                case OK: {
                    System.out.println("Created task " + response.getId());
                    break;
                }
                case INVALID: {
                    System.out.println("Invalid task " + response.getId());
                    break;
                }
                case BAD_NAMESPACE: {
                    System.out.println("Namespace does not exist for task " + task.getId());
                    break;
                }
                case DUPLICATE: {
                    System.out.println("Duplicate task " + response.getId());
                    break;
                }
                case FORBIDDEN_NAMESPACE: {
                    System.out.println("Permission denied for namespace " + response.getId().getNamespace() + " (task " + response.getId() + ")");
                    break;
                }
            }
        }
        return 0;
    }
}
