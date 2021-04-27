package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

public abstract class PodWithPorts extends Pod {

    private final Set<PortMapping> ports;

    /**
     * @param type the type of pod
     * @param id   the pod id
     */
    public PodWithPorts(@JsonProperty("type") final String type,
                        @JsonProperty("id") final String id,
                        @JsonProperty("ports") final Set<PortMapping> ports) {
        super(type, id);
        this.ports = ports;
    }

    public Set<PortMapping> getPorts() {
        return ports;
    }
}
