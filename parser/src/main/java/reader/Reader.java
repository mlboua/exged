package reader;

import data.Data;
import exception.ExgedParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface Reader {

    public Data readFile(File file);

    public Stream<Data> readFiles(List<File> files);

    public Stream<Data> readFilesParallel(List<File> files);

    public Stream<Data> readFolder(File directory) throws ExgedParserException;

    public Stream<Data> readFolderParallel(File directory) throws IOException, ExgedParserException;
}
