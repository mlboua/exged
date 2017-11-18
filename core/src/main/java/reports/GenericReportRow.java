package reports;

import data.Fold;

public class GenericReportRow {

    private final String rejectCode;
    private final String detailReject;
    private final Fold   fold;

    public GenericReportRow(final String rejectCode, final String detailReject, final Fold fold) {
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
        return "GenericReportRow{" + "rejectCode='" + rejectCode + '\'' + ", detailReject='" + detailReject + '\''
                + ", fold=" + fold + '}';
    }
}
