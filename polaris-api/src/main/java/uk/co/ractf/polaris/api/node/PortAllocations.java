package uk.co.ractf.polaris.api.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortAllocations extends JsonRepresentable {

    private final List<Integer> tcp;
    private final List<Integer> udp;

    public PortAllocations(
            @JsonProperty("tcp") final List<Integer> tcp,
            @JsonProperty("udp") final List<Integer> udp) {
        this.tcp = tcp;
        this.udp = udp;
    }

    public List<Integer> getTcp() {
        return tcp;
    }

    public List<Integer> getUdp() {
        return udp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PortAllocations)) return false;
        final PortAllocations that = (PortAllocations) o;
        return Objects.equals(tcp, that.tcp) && Objects.equals(udp, that.udp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tcp, udp);
    }

    public static PortAllocations empty() {
        return new PortAllocations(new ArrayList<>(), new ArrayList<>());
    }

}
