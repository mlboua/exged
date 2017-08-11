package data.stream;

import data.Data;
import data.Fold;
import identifier.csv.CsvIdentifier;

import java.util.List;
import java.util.stream.Stream;

public class StreamData implements Data {

    private final List<String> headers;
    private final List<CsvIdentifier> identifiers;
    private final Stream<List<String>> Rows;

    public StreamData(List<CsvIdentifier> identifiers, Stream<List<String>> rows) {
        this.identifiers = identifiers;
        Rows = rows;
        headers = null;
    }

    public StreamData(List<String> headers, List<CsvIdentifier> identifiers, Stream<List<String>> rows) {
        this.headers = headers;
        this.identifiers = identifiers;
        Rows = rows;
    }

    @Override
    public Stream<List<String>> stream() {
        return Rows;
    }

    @Override
    public Stream<Fold> foldStream() {
        return null;
    }
}
