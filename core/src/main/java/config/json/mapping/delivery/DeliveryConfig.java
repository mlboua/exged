package config.json.mapping.delivery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class DeliveryConfig {
    private final String name;
    private Optional<DeliveryType> type = Optional.empty();
    private Optional<String> sort = Optional.empty();
    private Optional<String> outputFolder = Optional.empty();

    @JsonCreator
    public DeliveryConfig(
            @JsonProperty("name") final String name,
            @JsonProperty("type") final Optional<DeliveryType> type,
            @JsonProperty("sort") final Optional<String> sort,
            @JsonProperty("outputFolder") final Optional<String> outputFolder) {
        this.name = name;
        this.type = type;
        this.sort = sort;
        this.outputFolder = outputFolder;
    }

    public String getName() {
        return name;
    }

    public Optional<DeliveryType> getType() {
        return type;
    }

    public Optional<String> getSort() {
        return sort;
    }

    public Optional<String> getOutputFolder() {
        return outputFolder;
    }

    @Override
    public String toString() {
        return "DeliveryConfig{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                ", outputFolder=" + outputFolder +
                '}';
    }
}
