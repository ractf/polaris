package uk.co.ractf.polaris.cli;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.Challenge;

import java.util.ArrayList;
import java.util.concurrent.Callable;

@CommandLine.Command()
public class Main implements Callable<Integer> {

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.out.println(new Challenge("aa", new ArrayList<>()).toJsonString());
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        return 69;
    }
}
