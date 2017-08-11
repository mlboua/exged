package config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class GenericNameWithArguments {
    private final String name;
    private Optional<List<String>> arguments;

    public GenericNameWithArguments(
            @JsonProperty("name") final String name,
            @JsonProperty("arguments") final Optional<List<String>> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public Optional<List<String>> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "GenericNameWithArguments{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
