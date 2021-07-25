package uk.co.ractf.polaris.tests;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.apiclient.APIClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationTest extends IntegrationTest {

    @Test
    public void testRootCredentials() {
        final var apiClient = APIClient.create("http://127.0.0.1:8080", "polaris", "polaris");
        assertEquals("polaris", apiClient.ping().exec().get("username"));
    }

}
