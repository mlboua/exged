package writer.reports;

import data.Fold;
import reader.config.mapping.reports.ReportsConfig;

import java.util.List;
import java.util.stream.Stream;

public interface ReportWriter {

    void createReport(ReportsConfig reportsConfig, List<Fold> foldList);

    void createReport(ReportsConfig reportsConfig, Stream<Fold> foldList);
}
