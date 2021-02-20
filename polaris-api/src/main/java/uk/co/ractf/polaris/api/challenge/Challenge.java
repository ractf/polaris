package uk.co.ractf.polaris.api.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.pod.Pod;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge {

    private final String id;
    private final List<Pod> pods;

    public Challenge(@JsonProperty("id") final String id, @JsonProperty("pods") final List<Pod> pods) {
        this.id = id;
        this.pods = pods;
    }

    public String getId() {
        return id;
    }

    public List<Pod> getPods() {
        return pods;
    }

    @JsonIgnoreProperties
    public Pod getPod(final String id) {
        for (final Pod pod : pods) {
            if (pod.getId().equals(id)) {
                return pod;
            }
        }

        return null;
    }

}
