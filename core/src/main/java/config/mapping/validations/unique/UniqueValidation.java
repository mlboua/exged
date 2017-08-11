package config.mapping.validations.unique;


import com.fasterxml.jackson.annotation.JsonProperty;
import config.mapping.validators.Validators;

import java.util.Optional;

public class UniqueValidation {
    private final String name;
    private Optional<Validators> validators = Optional.empty();

    public UniqueValidation(
            @JsonProperty("name") final String name,
            @JsonProperty("validators") final Optional<Validators> validators) {
        this.name = name;
        this.validators = validators;
    }

    public String getName() {
        return name;
    }

    public Optional<Validators> getValidators() {
        return validators;
    }

    @Override
    public String toString() {
        return "UniqueValidation{" +
                "name='" + name + '\'' +
                ", validators=" + validators +
                '}';
    }
}
