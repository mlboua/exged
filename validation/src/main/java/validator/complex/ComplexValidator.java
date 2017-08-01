package validator.complex;

import java.util.List;

// Utilisé pour les JSON
public class ComplexValidator {

    private String name;
    private List<String> arguments;

    public ComplexValidator() {
        name = null;
        arguments = null;
    }

    public ComplexValidator(String name, List<String> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ComplexValidator{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
