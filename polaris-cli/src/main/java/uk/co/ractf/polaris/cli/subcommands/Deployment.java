package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "deployment", aliases = {"d"}, subcommands = {

})
public class Deployment implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return null;
    }
}
