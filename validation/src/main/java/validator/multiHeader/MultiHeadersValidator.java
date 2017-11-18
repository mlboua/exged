package validator.multiHeader;

import java.util.List;

public class MultiHeadersValidator {

    private String name;
    private List<String> headers;
    private List<String> arguments;

    public MultiHeadersValidator() {
    }

    public MultiHeadersValidator(String name, List<String> headers, List<String> arguments) {
        this.name = name;
        this.headers = headers;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
