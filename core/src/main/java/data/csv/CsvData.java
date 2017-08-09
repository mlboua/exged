package data.csv;

import data.Data;
import data.Fold;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;

import java.util.*;
import java.util.stream.Stream;

public class CsvData implements Data {

    private final Map<String, Integer> headers;
    private final List<CsvIdentifier> identifiers;
    private final List<List<String>> rows;

    public CsvData(List<CsvIdentifier> identifiers, List<List<String>> rows) {
        this.identifiers = identifiers;
        this.rows = rows;
        this.headers = null;
    }

    public CsvData(List<CsvIdentifier> identifiers, Map<String, Integer> headers, List<List<String>> rows) {
        this.headers = headers;
        this.identifiers = identifiers;
        this.rows = rows;
        identifiers.forEach(csvIdentifier -> csvIdentifier.detectIdentifierIndex(headers));
    }

    public int getIndexByHeader(String header) {
        return headers != null ? headers.get(header) : -1;
    }

    public Map<String, Integer> getHeaders() {
        return headers;
    }

    @Override
    public Stream<List<String>> stream() {
        return rows.stream();
    }

    @Override
    public Stream<Fold> foldStream() {
        Stream.Builder<Fold> streamFold = Stream.builder();
        final CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
        final Map<String, List<List<String>>> foldTemp = new HashMap<>();
        this.rows.forEach(row -> {
            Optional<CsvIdentifier> identifier = csvIdentifierValidation.validate(identifiers, row);
            if (identifier.isPresent()) {
                String idRow = row.get(identifier.get().getIndex());
                if (foldTemp.containsKey(idRow)) {
                    foldTemp.get(idRow).add(row);
                } else {
                    List<List<String>> tempData = new ArrayList<>();
                    tempData.add(row);
                    foldTemp.put(idRow, tempData);
                }
            }
        });
        foldTemp.forEach((id, rows) -> streamFold.add(new CsvFold(id, rows, headers)));
        return streamFold.build();
    }

    @Override
    public String toString() {
        return "CsvData{" +
                "headers.size()=" + headers.keySet().size() +
                ", rows.size()=" + rows.size() +
                '}';
    }
}
