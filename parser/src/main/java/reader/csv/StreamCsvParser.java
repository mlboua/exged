package reader.csv;

import cyclops.async.LazyReact;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import data.Fold;
import data.FoldStatus;
import data.csv.CsvFold;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;
import org.pmw.tinylog.Logger;
import org.simpleflatmapper.csv.CsvParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class StreamCsvParser {

    private List<CsvIdentifier> csvIdentifiers;

    public StreamCsvParser(List<CsvIdentifier> csvIdentifiers) {
        this.csvIdentifiers = csvIdentifiers;
    }

    private static List<String> toArrayList(String[] row) {
        return new ArrayList<>(Arrays.asList(row));
    }

    public ReactiveSeq<Fold> readFileToFold(File file) {
        try {
            final Map<String, Integer> headers = getHeaders(getHeader(file, ","));
            try (FutureStream<String[]> futureRowStream = new LazyReact(10, 12)
                    .autoOptimizeOn()
                    .fromStream(CsvParser.stream(new FileReader(file)).skip(1))) {

                final CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
                return futureRowStream.map(StreamCsvParser::toArrayList)
                        .groupBy(row -> row.get(csvIdentifierValidation.validate(csvIdentifiers, row).orElseGet(null).getIndex()))
                        .stream()
                        .map(tupleIdRow -> new CsvFold(tupleIdRow.v1, tupleIdRow.v2, headers, FoldStatus.NOTTREATED));
            }
        } catch (IOException e) {
            Logger.error("Impossible de lire le fichier CSV: " + file.getName() + " - " + e);
        }
        return null;
    }

    private Map<String, Integer> getHeaders(String[] headers) {
        final Map<String, Integer> headersMap = new LinkedHashMap<>();
        IntStream.range(0, headers.length).forEach(key -> headersMap.put(headers[key], key));
        return headersMap;
    }

    private String[] getHeader(File file, String seperator) throws IOException {
        return Files.lines(file.toPath())
                .findFirst()
                .map(headers -> headers.split(seperator))
                .orElseGet(() -> new String[0]);
    }

    public FutureStream<Fold> readFolderParallel(File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return new LazyReact(100, 110).fromStream(Files.list(directory.toPath()))
                    .filter(path -> path.toString().endsWith(".csv"))
                    .async()
                    .map(Path::toFile)
                    .flatMap(this::readFileToFold);
        } catch (IOException e) {
            throw new ExgedParserException("Impossible de trouver le fichier ou les droits d'écriture ne sont pas attribué");
        }
    }

    private void verifyFolder(File directory) throws ExgedParserException, IOException {
        if (!directory.isDirectory()) {
            throw new ExgedParserException("Le fichier donné en paramètre n'est pas un dossier");
        }
        if (Files.list(directory.toPath()).count() == 0) {
            throw new ExgedParserException("Dossier d'entrée vide.");
        }
    }
}
