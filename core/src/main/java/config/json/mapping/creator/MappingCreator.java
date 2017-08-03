package config.json.mapping.creator;

import java.util.List;

public class MappingCreator {
    private String name;
    private String creator;
    private List<String> headers;
    private List<String> arguments;

    public MappingCreator() {
    }

    public MappingCreator(String name, String creator, List<String> headers, List<String> arguments) {
        this.name = name;
        this.creator = creator;
        this.headers = headers;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "MappingCreator{" +
                "name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", headers=" + headers +
                ", arguments=" + arguments +
                '}';
    }
}
