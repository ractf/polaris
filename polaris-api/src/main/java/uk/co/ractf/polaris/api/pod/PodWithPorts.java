package uk.co.ractf.polaris.api.pod;

import java.util.List;

public interface PodWithPorts {
    List<PortMapping> getPorts();
}
