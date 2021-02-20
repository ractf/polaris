package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.instance.Instance;

public class InstanceResponse {

    private final String ip;
    private final Instance instance;

    public InstanceResponse(
            @JsonProperty("ip") final String ip,
            @JsonProperty("instance") final Instance instance) {
        this.ip = ip;
        this.instance = instance;
    }

    public String getIp() {
        return ip;
    }

    public Instance getInstance() {
        return instance;
    }
}
