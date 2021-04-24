package uk.co.ractf.polaris.cli;

import com.fasterxml.jackson.databind.ext.Java7Handlers;
import picocli.CommandLine;
import uk.co.ractf.polaris.cli.subcommands.Login;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

@CommandLine.Command(
        subcommands = {
                Login.class
        }
)
public class Main implements Callable<Integer> {

    public static void main(final String[] args) {
        silenceJackson();
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private static void silenceJackson() {
        final PrintStream syserr = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(final int b) {
            }
        }));
        Java7Handlers.instance();
        System.setErr(syserr);
    }

    @Override
    public Integer call() {
        return 0;
    }
}
