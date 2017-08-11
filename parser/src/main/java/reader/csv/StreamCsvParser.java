package reader.csv;

import data.Data;
import exception.ExgedParserException;
import reader.Reader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class StreamCsvParser implements Reader {

    @Override
    public Data readFile(File file) {
        /*try (Stream<String[]> rowStream = CsvParser.stream(file)) {
            return new StreamData(rowStream.map(Arrays::asList), CsvParser.stream(file).limit(1));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    public Stream<Data> readFiles(List<File> files) {
        return null;
    }

    @Override
    public Stream<Data> readFilesParallel(List<File> files) {
        return null;
    }

    @Override
    public Stream<Data> readFolder(File directory) throws ExgedParserException {
        return null;
    }

    @Override
    public Stream<Data> readFolderParallel(File directory) throws IOException, ExgedParserException {
        return null;
    }

}
