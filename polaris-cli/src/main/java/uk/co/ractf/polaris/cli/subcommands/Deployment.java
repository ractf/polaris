package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.subcommands.deployment.DeploymentSubmit;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "deployment", aliases = {"d"}, subcommands = {
        DeploymentSubmit.class
})
public class Deployment implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return null;
    }
}
