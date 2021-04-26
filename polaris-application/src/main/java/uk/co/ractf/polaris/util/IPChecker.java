package uk.co.ractf.polaris.util;

import com.google.common.base.Charsets;
import com.google.common.net.InetAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class IPChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPChecker.class);

    private static final String[] IP_SERVERS = new String[]{
            "http://checkip.amazonaws.com",
            "http://ip.me",
            "http://ipecho.net/plain",
            "http://icanhazip.com",
            "http://ifconfig.me",
            "http://ident.me"
    };

    private IPChecker() {
    }

    public static String getExternalIP() {
        for (final String ipServer : IP_SERVERS) {
            try {
                final URL url = new URL(ipServer);
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
                    final String line = bufferedReader.readLine();
                    if (InetAddresses.isInetAddress(line)) {
                        return line;
                    }
                } catch (final IOException exception) {
                    LOGGER.error("Error getting ip", exception);
                }
            } catch (final MalformedURLException exception) {
                LOGGER.error("Error getting ip", exception);
            }
        }
        throw new IllegalStateException("Could not find ip address");
    }

}
