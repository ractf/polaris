package uk.co.ractf.polaris;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitInfo {

    public static String getGitCommit() {
        try {
            final var head = Files.readAllLines(Path.of("..", ".git", "HEAD")).get(0).substring(5);
            return Files.readAllLines(Path.of("..", ".git", head)).get(0).substring(0, 7);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
