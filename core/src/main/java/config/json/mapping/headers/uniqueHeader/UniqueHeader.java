package config.json.mapping.headers.uniqueHeader;


import config.json.GenericNameWithArguments;
import config.json.mapping.headers.Validators;

import java.util.Optional;

public class UniqueHeader {
    private String name;
    private Optional<Validators> validators = Optional.empty();
    private Optional<GenericNameWithArguments> converter = Optional.empty();
    private Optional<String> rename = Optional.empty();

    public UniqueHeader() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<Validators> getValidators() {
        return validators;
    }

    public void setValidators(Optional<Validators> validators) {
        this.validators = validators;
    }

    public Optional<GenericNameWithArguments> getConverter() {
        return converter;
    }

    public void setConverter(Optional<GenericNameWithArguments> converter) {
        this.converter = converter;
    }

    public Optional<String> getRename() {
        return rename;
    }

    public void setRename(Optional<String> rename) {
        this.rename = rename;
    }

    @Override
    public String toString() {
        return "UniqueHeader{" +
                "name='" + name + '\'' +
                ", validators=" + validators +
                ", converter=" + converter +
                ", rename=" + rename +
                '}';
    }
}
