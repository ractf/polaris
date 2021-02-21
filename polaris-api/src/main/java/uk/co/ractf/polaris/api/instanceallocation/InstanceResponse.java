package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.instance.Instance;

/**
 * The reponse to an {@link InstanceRequest}, contains the host ip and an {@link Instance} object
 *
 * <pre>
 *     {
 *         "ip": "8.8.8.8",
 *         "instance": {
 *             "id": "39b8db8f-c071-4aeb-aee3-147c4219688b",
 *             "deployment": "exampleDeployment1",
 *             "challenge": "hello-world",
 *             "host": "embedded"
 *         }
 *     }
 * </pre>
 */
public class InstanceResponse extends JsonRepresentable {

    private final String ip;
    private final Instance instance;

    /**
     * @param ip the ip the instance is on
     * @param instance the instance details
     */
    @Contract(pure = true)
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
