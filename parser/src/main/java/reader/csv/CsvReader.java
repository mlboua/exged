package reader.csv;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import data.Data;
import data.csv.CsvData;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import identifier.csv.CsvIdentifierValidation;
import reader.Reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static writer.csv.CsvWrite.writeCsvFile;

public class CsvReader implements Reader {

    private final Boolean headersExtraction;
    private final List<CsvIdentifier> csvIdentifiers;

    public CsvReader(Boolean headersExtraction, List<CsvIdentifier> csvIdentifiers) {
        this.headersExtraction = headersExtraction;
        this.csvIdentifiers = csvIdentifiers;
    }

    private CsvParser createCsvParser(RowListProcessor rowProcessor) {
        final CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(headersExtraction);
        return new CsvParser(parserSettings);
    }

    private List<List<String>> convertRowsToArrayList(RowListProcessor rowProcessor) {
        final List<List<String>> rows = new ArrayList<>();
        rowProcessor
                .getRows()
                .forEach(row -> rows.add(Arrays.stream(row).map(string -> string == null ? "" : string).collect(Collectors.toList())));
        return rows;
    }

    private Map<String, Integer> getHeaders(String[] headers) {
        final Map<String, Integer> headersMap = new HashMap<>();
        IntStream.range(0, headers.length).forEach(
                key -> headersMap.put(headers[key], key));
        return headersMap;
    }

    public Long countRows(File file) throws IOException {
        return Files.lines(file.toPath()).count();
    }

    public Long countRowsFolder(File directory) throws IOException {
        return Files.list(directory.toPath())
                .filter(path -> path.toString().endsWith(".csv"))
                .parallel()
                .mapToLong(path -> {
                    try {
                        return countRows(path.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }).sum();
    }

    public List<File> splitFile(File file, File outputFolder, int numberForEachFile, List<CsvIdentifier> identifiers) throws ExgedParserException {
        final RowListProcessor rowProcessor = new RowListProcessor();
        final CsvParser parser = createCsvParser(rowProcessor);
        final List<File> files = new ArrayList<>();
        parser.beginParsing(file);

        if (headersExtraction) {
            final Map<String, Integer> headers = getHeaders(parser.getContext().selectedHeaders());
            identifiers.forEach(identifier -> identifier.detectIdentifierIndex(headers));

            final List<String[]> rows = new ArrayList<>(numberForEachFile + 20);
            String idFold = "firstValue";

            final ResultIterator<String[], ParsingContext> iterator = parser.iterate(file).iterator();
            for (Iterator<String[]> iter = iterator; iterator.hasNext(); ) {
                String[] row = iter.next();
                if (rows.size() >= numberForEachFile) {
                    CsvIdentifierValidation csvIdentifierValidation = new CsvIdentifierValidation();
                    Optional<CsvIdentifier> identifier = csvIdentifierValidation.validate(identifiers, Arrays.asList(row));
                    if(identifier.isPresent()) {
                        final int indexId = identifier.get().getIndex();
                        if ("firstValue".equals(idFold)) {
                            idFold = row[indexId];
                        } else if (!idFold.equals(row[indexId])) {
                            File fileSplit = new File(outputFolder.getPath() + File.separator + file.getName().substring(0, file.getName().length() - 4) + "-" + files.size() + ".csv");
                            writeCsvFile(fileSplit, parser.getContext().selectedHeaders(), rows);
                            System.out.println("New file created " + fileSplit.getName());
                            files.add(fileSplit);
                            rows.clear();
                            System.gc();
                        }
                        rows.add(row);
                    } else {
                        throw new ExgedParserException("Impossible de trouvé l'ID pour la ligne (" + row + ")");
                    }
                } else {
                    rows.add(row);
                }
            }
            if (!rows.isEmpty()) {
                File fileSplit = new File(outputFolder.getPath() + File.separator + file.getName().substring(0, file.getName().length() - 4) + "-" + files.size() + ".csv");
                writeCsvFile(fileSplit, parser.getContext().selectedHeaders(), rows);
            }
        } else {
            throw new ExgedParserException("Il faut les en-têtes du fichier CSV afin de le diviser");
        }
        parser.stopParsing();
        return files;
    }

    @Override
    public Data readFile(File file) {
        final RowListProcessor rowProcessor = new RowListProcessor();
        final CsvParser parser = createCsvParser(rowProcessor);
        parser.parse(file);

        final List<List<String>> rows = convertRowsToArrayList(rowProcessor);

        if (headersExtraction) {
            final Map<String, Integer> headers = getHeaders(parser.getContext().selectedHeaders());
            return new CsvData(this.csvIdentifiers, headers, Collections.unmodifiableList(rows));
        }
        return new CsvData(this.csvIdentifiers, Collections.unmodifiableList(rows));

    }

    @Override
    public Stream<Data> readFiles(List<File> files) {
        return files.stream().map(file -> readFile(file));
    }

    @Override
    public Stream<Data> readFilesParallel(List<File> files) {
        return files.parallelStream().map(file -> readFile(file));
    }

    @Override
    public Stream<Data> readFolder(File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return Files
                    .list(directory.toPath())
                    .filter(path -> path.toString().endsWith(".csv"))
                    .map(file -> readFile(file.toFile()));
        } catch (IOException e) {
            throw new ExgedParserException("Impossible de trouver le fichier ou les droits d'écriture ne sont pas attribué");
        }
    }

    @Override
    public Stream<Data> readFolderParallel(File directory) throws ExgedParserException {
        try {
            verifyFolder(directory);
            return Files
                    .list(directory.toPath())
                    .filter(path -> path.toString().endsWith(".csv"))
                    .parallel()
                    .map(file -> readFile(file.toFile()));
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


