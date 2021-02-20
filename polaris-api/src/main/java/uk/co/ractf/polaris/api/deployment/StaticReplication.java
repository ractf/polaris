package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaticReplication extends Replication {

    private final Integer amount;

    public StaticReplication(
            @JsonProperty("type") final String type,
            @JsonProperty("amount") final Integer amount) {
        super(type);
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

}
