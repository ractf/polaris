package uk.co.ractf.polaris.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PolarisVersion {

    public static final String VERSION;

    static {
        final var in = PolarisVersion.class.getResourceAsStream("/version.txt");
        var version = "?";
        try(final var reader = new BufferedReader(new InputStreamReader(in))) {
            version = reader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        VERSION = version;
    }

    private PolarisVersion() {}

}
