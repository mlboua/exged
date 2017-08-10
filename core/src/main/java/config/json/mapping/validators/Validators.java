package config.json.mapping.validators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import config.json.GenericNameWithArguments;

import java.util.List;
import java.util.Optional;

public class Validators {
    private Optional<List<String>> simple = Optional.empty();
    private Optional<List<GenericNameWithArguments>> complex = Optional.empty();

    @JsonCreator
    public Validators(
            @JsonProperty("simple") final Optional<List<String>> simple,
            @JsonProperty("complex") final Optional<List<GenericNameWithArguments>> complex) {
        this.simple = simple;
        this.complex = complex;
    }

    public Optional<List<String>> getSimple() {
        return simple;
    }

    public void setSimple(Optional<List<String>> simple) {
        this.simple = simple;
    }

    public Optional<List<GenericNameWithArguments>> getComplex() {
        return complex;
    }

    public void setComplex(Optional<List<GenericNameWithArguments>> complex) {
        this.complex = complex;
    }

    @Override
    public String toString() {
        return "Validators{" +
                "simple=" + simple +
                ", complex=" + complex +
                '}';
    }
}
