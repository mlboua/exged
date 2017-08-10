package writer.reports;

import config.json.mapping.reports.ReportsConfig;
import data.Fold;

import java.util.List;
import java.util.stream.Stream;

public interface ReportWriter {

    void createReport(ReportsConfig reportsConfig, List<Fold> foldList);

    void createReport(ReportsConfig reportsConfig, Stream<Fold> foldList);
}
