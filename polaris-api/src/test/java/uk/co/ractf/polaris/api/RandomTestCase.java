package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.random.RandomEnvInteger;
import uk.co.ractf.polaris.api.random.RandomEnvString;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class RandomTestCase {

    @Test
    public void testInt() {
        validateObject(RandomEnvInteger.class, fixture("fixtures/random/int.json"));
    }

    @Test
    public void testStr() {
        validateObject(RandomEnvString.class, fixture("fixtures/random/str.json"));
    }

    @Test
    public void testIntIgnoreProperties() {
        validateObjectIgnoreProperties(RandomEnvInteger.class, fixture("fixtures/random/int.json"));
    }

    @Test
    public void testStrIgnoreProperties() {
        validateObjectIgnoreProperties(RandomEnvString.class, fixture("fixtures/random/str.json"));
    }

}
