package uk.co.ractf.polaris.cli;

import uk.co.ractf.polaris.apiclient.APIClient;

public class APIClientFactory {

    public static APIClient createAPIClient() {
        final var cliConfig = CLIConfig.readFromFile();
        return APIClient.create(cliConfig.getHost(), cliConfig.getUsername(), cliConfig.getPassword());
    }

}
