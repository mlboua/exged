package config.json.mapping.validations.global;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import config.json.GenericNameWithArguments;

import java.util.List;

public class GlobalValidation {
    private final String name;
    private final List<String> headers;
    private final List<GenericNameWithArguments> validators;

    @JsonCreator
    public GlobalValidation(
            @JsonProperty("name") final String name,
            @JsonProperty("headers") final List<String> headers,
            @JsonProperty("validators") final List<GenericNameWithArguments> validators) {
        this.name = name;
        this.headers = headers;
        this.validators = validators;
    }

    public String getName() {
        return name;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<GenericNameWithArguments> getValidators() {
        return validators;
    }

    @Override
    public String toString() {
        return "GlobalValidation{" +
                "name='" + name + '\'' +
                ", headers=" + headers +
                ", validators=" + validators +
                '}';
    }
}
