package config.mapping.reports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import data.FoldStatus;
import util.FileType;

import java.util.List;

public class ReportsConfig {
    private final String name;
    private final FileType type;
    private final FoldStatus foldStatus;
    private final List<String> headers;

    @JsonCreator
    public ReportsConfig(
            @JsonProperty("name") final String name,
            @JsonProperty("type") final FileType type,
            @JsonProperty("foldStatus") final FoldStatus foldStatus,
            @JsonProperty("headers") final List<String> headers) {
        this.name = name;
        this.type = type;
        this.foldStatus = foldStatus;
        this.headers = headers;
    }

    public String getName() {
        return name;
    }

    public FileType getType() {
        return type;
    }

    public FoldStatus getFoldStatus() {
        return foldStatus;
    }

    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "ReportsConfig{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", foldStatus=" + foldStatus +
                ", headers=" + headers +
                '}';
    }
}
