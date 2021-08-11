package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a randomly generated string environment variable
 *
 * <pre>
 *     {
 *         "type": "string",
 *         "alphabet": "abcdef1234567890",
 *         "length": 16
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomEnvString extends RandomEnv {

    private final String alphabet;
    private final Integer length;

    /**
     * @param type     the type of random env (string)
     * @param alphabet the alphabet to generate from
     * @param length   the length of the string
     */
    @Contract(pure = true)
    public RandomEnvString(
            @JsonProperty("type") final String type,
            @JsonProperty("display") final String display,
            @JsonProperty("alphabet") final String alphabet,
            @JsonProperty("length") final Integer length) {
        super(type, display);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RandomEnvString that = (RandomEnvString) o;
        return Objects.equals(alphabet, that.alphabet) && Objects.equals(length, that.length) && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(alphabet, length, getType());
    }
}
