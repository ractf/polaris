package uk.co.ractf.polaris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GitInfo {

    public static String getGitCommit() {
        try {
            final var process = Runtime.getRuntime().exec("git rev-parse --short HEAD");
            try(final var reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.readLine();
            }
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to run command", exception);
        }
    }

}
