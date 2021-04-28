package uk.co.ractf.polaris.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PolarisVersion {

    public static final String VERSION;

    static {
        final InputStream in = PolarisVersion.class.getResourceAsStream("/version.txt");
        String version = "?";
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            version = reader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        VERSION = version;
    }

    private PolarisVersion() {}

}
