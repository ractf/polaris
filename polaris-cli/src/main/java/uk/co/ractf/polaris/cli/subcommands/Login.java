package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.Subcommand;

@CommandLine.Command(name = "login")
public class Login extends Subcommand {

    @CommandLine.Option(names = {"-u", "--user"}, description = "Username")
    String user;

    @CommandLine.Option(names = {"-p", "--password"}, description = "Password", interactive = true)
    char[] password;

    @Override
    public int run() throws Exception {
        System.out.println(user);
        System.out.println(password);
        return 0;
    }

}
