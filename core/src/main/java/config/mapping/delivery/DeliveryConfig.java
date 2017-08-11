package config.mapping.delivery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import config.GenericNameWithArguments;

import java.util.Optional;

public class DeliveryConfig {
    private final String name;
    private Optional<GenericNameWithArguments> type = Optional.empty();
    private Optional<GenericNameWithArguments> sort = Optional.empty();

    @JsonCreator
    public DeliveryConfig(
            @JsonProperty("name") final String name,
            @JsonProperty("type") final Optional<GenericNameWithArguments> type,
            @JsonProperty("sort") final Optional<GenericNameWithArguments> sortr) {
        this.name = name;
        this.type = type;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public Optional<GenericNameWithArguments> getType() {
        return type;
    }

    public Optional<GenericNameWithArguments> getSort() {
        return sort;
    }

    @Override
    public String toString() {
        return "DeliveryConfig{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                '}';
    }
}
