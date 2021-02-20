package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomEnvString extends RandomEnv {

    private final String alphabet;
    private final Integer length;

    public RandomEnvString(
            @JsonProperty("type") final String type,
            @JsonProperty("alphabet") final String alphabet,
            @JsonProperty("length") final Integer length) {
        super(type);
        this.alphabet = alphabet;
        this.length = length;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public Integer getLength() {
        return length;
    }

    @Override
    public String generate() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Random random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return stringBuilder.toString();
    }

}
