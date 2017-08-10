package config.json.mapping.validations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import config.json.mapping.validations.global.GlobalValidation;
import config.json.mapping.validations.unique.UniqueValidation;

import java.util.List;

public class ValidatorsConfig {

    private final List<GlobalValidation> global;
    private final List<UniqueValidation> unique;

    @JsonCreator
    public ValidatorsConfig(
            @JsonProperty("global") final List<GlobalValidation> global,
            @JsonProperty("unique") final List<UniqueValidation> unique) {
        this.global = global;
        this.unique = unique;
    }

    public List<GlobalValidation> getGlobal() {
        return global;
    }

    public List<UniqueValidation> getUnique() {
        return unique;
    }

    @Override
    public String toString() {
        return "MappingFold{" +
                "global=" + global +
                ", unique=" + unique +
                '}';
    }
}
