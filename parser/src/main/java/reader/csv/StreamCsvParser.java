package reader.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;
import org.simpleflatmapper.csv.CsvParser;

import cyclops.stream.ReactiveSeq;
import data.Fold;
import data.FoldStatus;
import data.csv.CsvFold;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;

public class StreamCsvParser {

    private final List<CsvIdentifier> csvIdentifiers;

    public StreamCsvParser(final List<CsvIdentifier> csvIdentifiers) {
        this.csvIdentifiers = csvIdentifiers;
    }

    private static List<String> toArrayList(final String[] row) {
        return new ArrayList<>(Arrays.asList(row));
    }

    public ReactiveSeq<Fold> readFileToFold(final File file) {
        try {
            final Map<String, Integer> headers = getHeaders(getHeader(file, ","));
            try (ReactiveSeq<String[]> futureRowStream = ReactiveSeq
                    .fromStream(CsvParser.stream(new FileReader(file)).skip(1))) {
                final CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
                return futureRowStream.map(StreamCsvParser::toArrayList)
                        .groupBy(row -> row
                                .get(csvIdentifierValidation.validate(csvIdentifiers, row).orElseGet(null).getIndex()))
                        .stream().map(tupleIdRow -> new CsvFold(tupleIdRow.v1, tupleIdRow.v2,
                                new LinkedHashMap<>(headers), FoldStatus.NOTTREATED));
            }
        } catch (final IOException e) {
            Logger.error("Impossible de lire le fichier CSV: " + file.getName() + " - " + e);
        }
        return null;
    }

    private Map<String, Integer> getHeaders(final String[] headers) {
        final Map<String, Integer> headersMap = new LinkedHashMap<>();
        IntStream.range(0, headers.length).forEach(key -> headersMap.put(headers[key], key));
        return headersMap;
    }

    private String[] getHeader(final File file, final String seperator) throws IOException {
        return Files.lines(file.toPath()).findFirst().map(headers -> headers.split(seperator))
                .orElseGet(() -> new String[0]);
    }

    public Stream<Fold> readFolderParallel(final File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return Files.list(directory.toPath()).parallel().filter(path -> path.toString().endsWith(".csv"))
                    .map(Path::toFile).flatMap(this::readFileToFold);
        } catch (final IOException e) {
            throw new ExgedParserException(
                    "Impossible de trouver le fichier ou les droits d'écriture ne sont pas attribué");
        }
    }

    private void verifyFolder(final File directory) throws ExgedParserException, IOException {
        if (!directory.isDirectory()) {
            throw new ExgedParserException(
                    "Le fichier donné en paramètre n'est pas un dossier: " + directory.toPath().toString());
        }
        if (Files.list(directory.toPath()).count() == 0) {
            throw new ExgedParserException("Dossier d'entrée vide : " + directory.toPath().toString());
        }
    }

}
