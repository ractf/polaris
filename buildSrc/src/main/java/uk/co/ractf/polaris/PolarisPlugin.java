package uk.co.ractf.polaris;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PolarisPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getTasks().create("generateReflectConfig", GenerateReflectConfigTask.class);
    }
}
