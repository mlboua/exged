package reports;

import data.Fold;

public class GenericReportRow {

    private String rejectCode;
    private String detailReject;
    private Fold fold;

    public GenericReportRow(String rejectCode, String detailReject, Fold fold) {
        this.rejectCode = rejectCode;
        this.detailReject = detailReject;
        this.fold = fold;
    }

    public String getRejectCode() {
        return rejectCode;
    }

    public String getDetailReject() {
        return detailReject;
    }

    public Fold getFold() {
        return fold;
    }

    @Override
    public String toString() {
        return "GenericReportRow{" +
                "rejectCode='" + rejectCode + '\'' +
                ", detailReject='" + detailReject + '\'' +
                ", fold=" + fold +
                '}';
    }
}
