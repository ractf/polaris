package uk.co.ractf.polaris.host;

import com.github.dockerjava.api.model.PortBinding;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.PortMapping;

import java.util.List;
import java.util.Map;

public interface Host {

    String getId();

    Instance createInstance(final Challenge challenge, final Deployment deployment);

    void removeInstance(final Instance instance);

    Map<String, Instance> getInstances();

    void restartInstance(final Instance instance);

    Instance getInstance(final String id);

    Map<PortMapping, PortBinding> createPortBindings(List<PortMapping> portMappings);

    String getPublicIp();

}
