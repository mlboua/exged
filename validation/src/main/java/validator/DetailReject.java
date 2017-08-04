package validator;

import data.Fold;

import java.util.List;
import java.util.Optional;

public class DetailReject extends Reject {

    private String detail;
    private Fold fold;

    public DetailReject(String codeRejet, String detail, Fold fold) {
        super(codeRejet);
        this.detail = detail;
        this.fold = fold;
    }

    public DetailReject(String codeRejet, List<String> values, String detail, Fold fold) {
        super(codeRejet, values);
        this.detail = detail;
        this.fold = fold;
    }

    public DetailReject(String codeRejet, Optional<List<String>> values, String detail, Fold fold) {
        super(codeRejet, values);
        this.detail = detail;
        this.fold = fold;
    }

    public String getDetail() {
        return detail;
    }

    public Fold getFold() {
        return fold;
    }

    @Override
    public String toString() {
        return "DetailReject{" +
                "code='" + super.getCode() + '\'' +
                ", values=" + super.getValues() + '\'' +
                "detail='" + detail + '\'' +
                ", fold=" + fold +
                '}';
    }
}
