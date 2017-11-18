package reader.csv;

import static writer.csv.CsvWrite.writeCsvFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import config.Config;
import data.Data;
import data.csv.CsvData;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;
import reader.Reader;

public class CsvReader implements Reader {

    private final Boolean             headersExtraction;
    private final List<CsvIdentifier> csvIdentifiers;

    public CsvReader(final Boolean headersExtraction, final List<CsvIdentifier> csvIdentifiers) {
        this.headersExtraction = headersExtraction;
        this.csvIdentifiers = csvIdentifiers;
    }

    private CsvParser createCsvParser(final RowListProcessor rowProcessor) {
        final CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        final CsvFormat csv = new CsvFormat();
        csv.setDelimiter(Config.getMainConfig().getCsvDelimiter().charAt(0));
        parserSettings.setFormat(csv);
        parserSettings.setHeaderExtractionEnabled(headersExtraction);
        return new CsvParser(parserSettings);
    }

    private List<List<String>> convertRowsToArrayList(final RowListProcessor rowProcessor) {
        final List<List<String>> rows = new ArrayList<>();
        rowProcessor.getRows().forEach(row -> rows
                .add(Arrays.stream(row).map(string -> string == null ? "" : string).collect(Collectors.toList())));
        return rows;
    }

    private Map<String, Integer> getHeaders(final String[] headers) {
        final Map<String, Integer> headersMap = new LinkedHashMap<>();
        IntStream.range(0, headers.length).forEach(key -> headersMap.put(headers[key], key));
        return headersMap;
    }

    private Long countRows(final File file) {
        try {
            return Files.lines(file.toPath()).count() - 1;
        } catch (final IOException e) {
            Logger.error("Impossible de lire le fichier: " + e);
        }
        return 0L;
    }

    public String[] getHeader(final File file, final String seperator) throws IOException {
        return Files.lines(file.toPath()).findFirst().map(headers -> headers.split(seperator))
                .orElseGet(() -> new String[0]);
    }

    public Long countRowsFolder(final File directory) throws IOException {
        return Files.list(directory.toPath()).filter(path -> path.toString().endsWith(".csv")).parallel()
                .map(Path::toFile).mapToLong(this::countRows).sum();
    }

    public void splitFile(final File file, final File outputFolder, final int numberForEachFile,
            final List<CsvIdentifier> identifiers, final AtomicInteger numberSplittedFiles)
            throws ExgedParserException {
        final RowListProcessor rowProcessor = new RowListProcessor();
        final CsvParser parser = createCsvParser(rowProcessor);
        final AtomicInteger filesCounter = new AtomicInteger(0);
        parser.beginParsing(file);

        if (headersExtraction) {
            final Map<String, Integer> headers = getHeaders(parser.getContext().selectedHeaders());
            identifiers.forEach(identifier -> identifier.detectIdentifierIndex(headers));
            parser.stopParsing();
            final List<String[]> rows = new ArrayList<>(numberForEachFile + 20);
            final List<String> idFold = new ArrayList<>();
            idFold.add("firstValue");
            try (Stream<String[]> stream = org.simpleflatmapper.csv.CsvParser
                    .separator(Config.getMainConfig().getCsvDelimiter().charAt(0)).stream(new FileReader(file))
                    .skip(1)) {
                stream.forEach(strings -> {
                    if (rows.size() >= numberForEachFile) {
                        final CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
                        final Optional<CsvIdentifier> identifier = csvIdentifierValidation.validate(identifiers,
                                Arrays.asList(strings));
                        if (identifier.isPresent()) {
                            final int indexId = identifier.get().getIndex();
                            if ("firstValue".equals(idFold.get(0))) {
                                idFold.set(0, strings[indexId]);
                            } else if (!idFold.get(0).equals(strings[indexId])) {
                                final File fileSplit = new File(
                                        outputFolder.getPath() + File.separator + filesCounter.get() + "-"
                                                + file.getName().substring(0, file.getName().length() - 4) + ".csv");
                                writeCsvFile(fileSplit, parser.getContext().selectedHeaders(), rows);
                                filesCounter.getAndAdd(1);
                                rows.clear();
                                numberSplittedFiles.getAndAdd(1);
                            }
                            rows.add(strings);
                        } else {
                            Logger.error("Impossible de trouvé l'ID pour la ligne (" + Arrays.toString(strings) + ")");
                        }
                    } else {
                        rows.add(strings);
                    }
                });
            } catch (final IOException e) {
                System.out.println("EXCEPTION");
                e.printStackTrace();
            }
            if (!rows.isEmpty()) {
                final File fileSplit = new File(outputFolder.getPath() + File.separator
                        + file.getName().substring(0, file.getName().length() - 4) + "-" + filesCounter.get() + ".csv");
                writeCsvFile(fileSplit, parser.getContext().selectedHeaders(), rows);
            }

        } else {
            throw new ExgedParserException("Il faut les en-têtes du fichier CSV afin de le diviser");
        }
        parser.stopParsing();
    }

    @Override
    public Data readFile(final File file) {
        final RowListProcessor rowProcessor = new RowListProcessor();
        final CsvParser parser = createCsvParser(rowProcessor);
        parser.parse(file);
        final List<List<String>> rows = convertRowsToArrayList(rowProcessor);
        if (headersExtraction) {
            final Map<String, Integer> headers = getHeaders(parser.getContext().selectedHeaders());
            return new CsvData(csvIdentifiers, headers, Collections.unmodifiableList(rows));
        }
        return new CsvData(csvIdentifiers, Collections.unmodifiableList(rows));

    }

    @Override
    public Stream<Data> readFiles(final List<File> files) {
        return files.stream().map(this::readFile);
    }

    @Override
    public Stream<Data> readFilesParallel(final List<File> files) {
        return files.parallelStream().map(this::readFile);
    }

    @Override
    public Stream<Data> readFolder(final File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return Files.list(directory.toPath()).filter(path -> path.toString().endsWith(".csv")).map(Path::toFile)
                    .map(this::readFile);
        } catch (final IOException e) {
            throw new ExgedParserException(
                    "Impossible de trouver le fichier ou les droits d'écriture ne sont pas attribué");
        }
    }

    @Override
    public Stream<Data> readFolderParallel(final File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return Files.list(directory.toPath()).filter(path -> path.toString().endsWith(".csv")).parallel()
                    .map(Path::toFile).map(this::readFile);
        } catch (final IOException e) {
            throw new ExgedParserException(
                    "Impossible de trouver le fichier ou les droits d'écriture ne sont pas attribué");
        }
    }

    private void verifyFolder(final File directory) throws ExgedParserException, IOException {
        if (!directory.isDirectory()) {
            throw new ExgedParserException("Le fichier donné en paramètre n'est pas un dossier");
        }
        if (Files.list(directory.toPath()).count() == 0) {
            throw new ExgedParserException("Dossier d'entrée vide.");
        }
    }

    public Boolean getHeadersExtraction() {
        return headersExtraction;
    }

    public List<CsvIdentifier> getCsvIdentifiers() {
        return csvIdentifiers;
    }

}
