package uk.co.ractf.polaris;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateReflectConfigTask extends DefaultTask {

    @TaskAction
    public void run() throws IOException {
        final Path path = Path.of(getProject().getProjectDir().getAbsolutePath(), "src", "main", "java");
        if (!path.toFile().exists()) {
            return;
        }
        final List<Map<String, Object>> reflectConfig = new ArrayList<>();
        Files.walk(path)
                .filter(Files::isRegularFile)
                .filter(file -> {
                    try {
                        return Files.readString(file).contains("@JsonIgnoreProperties");
                    } catch (final IOException e) {
                        return false;
                    }
                })
                .forEach(file -> {
                    final String className = path.toUri().relativize(file.toUri()).getPath()
                            .replace(".java", "").replace("/", ".");
                    final Map<String, Object> classData = new HashMap<>();
                    classData.put("name", className);
                    classData.put("allDeclaredConstructors", true);
                    classData.put("allPublicConstructors", true);
                    classData.put("allDeclaredMethods", true);
                    classData.put("allPublicMethods", true);
                    reflectConfig.add(classData);
                });
        final Path configPath = Path.of(getProject().getBuildDir().getAbsolutePath(), "resources", "main",
                "META-INF", "native-image", (String) getProject().getGroup(), getProject().getName(),
                "reflect-config.json");
        if (configPath.toFile().exists()) {
            reflectConfig.addAll(new ObjectMapper().readValue(Files.readString(configPath), new TypeReference<List<Map<String, Object>>>() {
            }));
        } else {
            configPath.getParent().toFile().mkdirs();
        }
        Files.writeString(configPath, new ObjectMapper().writeValueAsString(reflectConfig));
    }

}
