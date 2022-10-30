package uk.co.ractf.polaris.cli;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CLIConfig extends JsonRepresentable {

    private final String host;
    private final String username;
    private final String password;

    public CLIConfig(
            @JsonProperty("host") final String host,
            @JsonProperty("username") final String username,
            @JsonProperty("password") final String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public static boolean doesFileExist() {
        return Path.of(System.getProperty("user.home"), ".polaris", "config.json").toFile().exists();
    }

    public static CLIConfig readFromFile() {
        try {
            return parse(new String(Files.readAllBytes(Path.of(System.getProperty("user.home"), ".polaris", "config.json"))), CLIConfig.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void save() {
        Path.of(System.getProperty("user.home"), ".polaris").toFile().mkdirs();
        try {
            Files.writeString(Path.of(System.getProperty("user.home"), ".polaris", "config.json"), toJsonString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
