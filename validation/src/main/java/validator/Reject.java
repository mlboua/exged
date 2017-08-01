package validator;

import java.util.List;
import java.util.Optional;

public class Reject {

    private final String code;
    private List<String> values;

    public Reject(String codeRejet) {
        this.code = codeRejet;
        this.values = null;
    }

    public Reject(String codeRejet, List<String> values) {
        this.code = codeRejet;
        this.values = values;
    }

    public String getCode() {
        return code;
    }

    public Optional<List<String>> getValues() {
        return values != null ? Optional.of(values) : Optional.empty();
    }
}
