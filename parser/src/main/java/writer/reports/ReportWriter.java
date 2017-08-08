package writer.reports;

import config.json.mapping.reports.MappingReports;
import data.Fold;

import java.util.List;
import java.util.stream.Stream;

public interface ReportWriter {

    void createReport(MappingReports mappingReports, List<Fold> foldList);

    void createReport(MappingReports mappingReports, Stream<Fold> foldList);
}
