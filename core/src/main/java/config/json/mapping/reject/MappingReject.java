package config.json.mapping.reject;

import config.json.mapping.validators.Validators;

public class MappingReject {

    private String code;
    private String detail;
    private Validators validators;

    public MappingReject() {
    }

    public MappingReject(String code, String detail, Validators validators) {
        this.code = code;
        this.detail = detail;
        this.validators = validators;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Validators getValidators() {
        return validators;
    }

    public void setValidators(Validators validators) {
        this.validators = validators;
    }

    @Override
    public String toString() {
        return "MappingReject{" +
                "code='" + code + '\'' +
                ", detail='" + detail + '\'' +
                ", validators=" + validators +
                '}';
    }
}
