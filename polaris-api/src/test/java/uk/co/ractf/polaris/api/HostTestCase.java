package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.host.HostInfo;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class HostTestCase {

    @Test
    public void testHostInfo() {
        validateObject(HostInfo.class, fixture("fixtures/host/info.json"));
    }

    @Test
    public void testHostInfoIgnoreProperties() {
        validateObjectIgnoreProperties(HostInfo.class, fixture("fixtures/host/info.json"));
    }

    @Test
    public void testHostInfoEquals() {
        EqualsVerifier.simple().forClass(HostInfo.class).verify();
    }

}
