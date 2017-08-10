package config.json.mapping.reject;

import config.json.mapping.validators.Validators;

public class RejectConfig {

    private final String code;
    private final String detail;
    private final Validators validators;

    public RejectConfig(String code, String detail, Validators validators) {
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
