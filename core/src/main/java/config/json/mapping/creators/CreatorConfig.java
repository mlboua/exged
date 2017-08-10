package config.json.mapping.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreatorConfig {
    private final String name;
    private final String creator;
    private final List<String> headers;
    private final List<String> arguments;

    @JsonCreator
    public CreatorConfig(
            @JsonProperty("name") final String name,
            @JsonProperty("creator") final String creator,
            @JsonProperty("headers") final List<String> headers,
            @JsonProperty("arguments") final List<String> arguments) {
        this.name = name;
        this.creator = creator;
        this.headers = headers;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "CreatorConfig{" +
                "name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", headers=" + headers +
                ", arguments=" + arguments +
                '}';
    }
}
