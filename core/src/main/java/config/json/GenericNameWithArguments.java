package config.json;

import java.util.List;
import java.util.Optional;

public class GenericNameWithArguments {
    private String name;
    private Optional<List<String>> arguments;

    public GenericNameWithArguments() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<List<String>> getArguments() {
        return arguments;
    }

    public void setArguments(Optional<List<String>> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "GenericNameWithArguments{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
