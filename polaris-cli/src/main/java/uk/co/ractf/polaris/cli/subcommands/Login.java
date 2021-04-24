package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.Subcommand;

@CommandLine.Command(name = "login")
public class Login extends Subcommand {

    @CommandLine.Option(names = {"-u", "--user"}, description = "Username")
    private String user;

    @CommandLine.Option(names = {"-p", "--password"}, description = "Password")
    private String password;

    @CommandLine.Parameters(index = "0")
    private String host;

    @CommandLine.Option(names = {"-P", "--port"}, defaultValue = "8080", description = "Port")
    private int port;

    @Override
    public int run() throws Exception {
        if (user == null) {
            System.out.printf("Login to %s%n", host);
            user = System.console().readLine("Username: ");
        }
        if (password == null) {
            password = String.valueOf(System.console().readPassword("Password: "));
        }
        return 0;
    }

}
