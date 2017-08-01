package validator.complex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Utilisé pour les JSON
public class ComplexValidator {

    private String name;
    private Optional<List<String>> arguments;

    public ComplexValidator() {
        name = null;
        arguments = Optional.empty();
    }

    public ComplexValidator(String name, List<String> arguments) {
        this.name = name;
        this.arguments = Optional.of(arguments);
    }

    public String getName() {
        return name;
    }

    public Optional<List<String>> getArguments() {
        return arguments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArguments(Optional<List<String>> arguments) {
        this.arguments = arguments;
    }
}
