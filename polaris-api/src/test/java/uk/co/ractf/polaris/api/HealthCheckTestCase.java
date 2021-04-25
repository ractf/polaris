package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.healthcheck.*;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class HealthCheckTestCase {

    @Test
    public void testCommand() {
        validateObject(CommandHealthCheck.class, fixture("fixtures/healthcheck/cmd.json"));
    }

    @Test
    public void testHttp() {
        validateObject(HttpHealthCheck.class, fixture("fixtures/healthcheck/http.json"));
    }

    @Test
    public void testExactTcp() {
        validateObject(ReceiveExactTcpPayload.class, fixture("fixtures/healthcheck/exacttcppayload.json"));
    }

    @Test
    public void testRegexTcp() {
        validateObject(ReceiveRegexTcpPayload.class, fixture("fixtures/healthcheck/regextcppayload.json"));
    }

    @Test
    public void testSendTcp() {
        validateObject(SendTcpPayload.class, fixture("fixtures/healthcheck/sendtcp.json"));
    }

    @Test
    public void testTcp() {
        validateObject(TcpHealthCheck.class, fixture("fixtures/healthcheck/tcp.json"));
    }

    @Test
    public void testTcpPayload() {
        validateObject(TcpPayloadHealthCheck.class, fixture("fixtures/healthcheck/tcppayload.json"));
    }

    @Test
    public void testCommandIgnoreProperties() {
        validateObjectIgnoreProperties(CommandHealthCheck.class, fixture("fixtures/healthcheck/cmd.json"));
    }

    @Test
    public void testHttpIgnoreProperties() {
        validateObjectIgnoreProperties(HttpHealthCheck.class, fixture("fixtures/healthcheck/http.json"));
    }

    @Test
    public void testExactTcpIgnoreProperties() {
        validateObjectIgnoreProperties(ReceiveExactTcpPayload.class, fixture("fixtures/healthcheck/exacttcppayload.json"));
    }

    @Test
    public void testRegexTcpIgnoreProperties() {
        validateObjectIgnoreProperties(ReceiveRegexTcpPayload.class, fixture("fixtures/healthcheck/regextcppayload.json"));
    }

    @Test
    public void testSendTcpIgnoreProperties() {
        validateObjectIgnoreProperties(SendTcpPayload.class, fixture("fixtures/healthcheck/sendtcp.json"));
    }

    @Test
    public void testTcpIgnoreProperties() {
        validateObjectIgnoreProperties(TcpHealthCheck.class, fixture("fixtures/healthcheck/tcp.json"));
    }

    @Test
    public void testTcpPayloadIgnoreProperties() {
        validateObjectIgnoreProperties(TcpPayloadHealthCheck.class, fixture("fixtures/healthcheck/tcppayload.json"));
    }

}
