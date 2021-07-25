package uk.co.ractf.polaris.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

public abstract class IntegrationTest {

    @BeforeAll
    public static void buildDocker() throws IOException, InterruptedException {
        new ProcessBuilder("docker-compose", "up", "-d")
                .directory(new File("src/test/resources/"))
                .inheritIO()
                .start()
                .waitFor();
        Thread.sleep(3000);
    }

    @AfterAll
    public static void teardown() throws IOException, InterruptedException {
        new ProcessBuilder("docker-compose", "rm", "-sf")
                .directory(new File("src/test/resources/"))
                .inheritIO()
                .start()
                .waitFor();
    }

}
