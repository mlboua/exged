package config.json.mapping.validators;

import config.json.GenericNameWithArguments;

import java.util.List;
import java.util.Optional;

public class Validators {
    private Optional<List<String>> simple = Optional.empty();
    private Optional<List<GenericNameWithArguments>> complex = Optional.empty();

    public Validators() {
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
