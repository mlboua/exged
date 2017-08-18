package reader.config.mapping.reject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import reader.config.mapping.validators.Validators;

public class RejectConfig {

    private final String code;
    private final String detail;
    private final Validators validators;

    @JsonCreator
    public RejectConfig(
            @JsonProperty("code") final String code,
            @JsonProperty("detail") final String detail,
            @JsonProperty("validators") final Validators validators) {
        this.code = code;
        this.detail = detail;
        this.validators = validators;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public Validators getValidators() {
        return validators;
    }

    @Override
    public String toString() {
        return "RejectConfig{" +
                "code='" + code + '\'' +
                ", detail='" + detail + '\'' +
                ", validators=" + validators +
                '}';
    }
}
