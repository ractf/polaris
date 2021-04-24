package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.CLIConfig;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "login")
public class Login implements Callable<Integer> {

    @CommandLine.Option(names = {"-u", "--username"}, description = "Username")
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, description = "Password")
    private String password;

    @CommandLine.Parameters(index = "0")
    private String host;

    @Override
    public Integer call() {
        if (host == null) {
            host = System.console().readLine("Host: ");
        }
        if (username == null) {
            System.out.printf("Login to %s%n", host);
            username = System.console().readLine("Username: ");
        }
        if (password == null) {
            password = String.valueOf(System.console().readPassword("Password: "));
        }
        if (!host.startsWith("http")) {
            host = "http://" + host;
        }

        new CLIConfig(host, username, password).save();
        return 0;
    }
}
