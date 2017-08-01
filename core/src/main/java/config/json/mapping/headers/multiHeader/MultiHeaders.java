package config.json.mapping.headers.multiHeader;

import config.json.mapping.headers.Validators;

import java.util.List;

public class MultiHeaders {
    private String name;
    private List<String> headers;
    private Validators validators;

    public MultiHeaders() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public Validators getValidators() {
        return validators;
    }

    public void setValidators(Validators validators) {
        this.validators = validators;
    }

    @Override
    public String toString() {
        return "MultiHeaders{" +
                "name='" + name + '\'' +
                ", headers=" + headers +
                ", validators=" + validators +
                '}';
    }
}
