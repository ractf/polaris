package uk.co.ractf.polaris.cli;

import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.subcommands.Login;

import java.util.concurrent.Callable;

public abstract class Subcommand implements Callable<Integer> {

    public abstract int run(final APIClient apiClient) throws Exception;

    @Override
    public Integer call() throws Exception {
        if (!CLIConfig.doesFileExist()) {
            new Login().call();
        }

        final var apiClient = APIClientFactory.createAPIClient();
        try {
            apiClient.ping().exec();
        } catch (final Exception e) {
            e.printStackTrace();
            authFail();
        }

        return run(apiClient);
    }

    private void authFail() {
        System.out.println("Something went wrong authenticating with polaris, run `polaris login` or try again.");
        System.exit(1);
    }

}
