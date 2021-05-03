package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.subcommands.task.TaskCreate;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "task", aliases = {"t"}, subcommands = {
        TaskCreate.class
})
public class Task implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return null;
    }
}
