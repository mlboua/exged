package reader;

import data.Data;
import exception.ExgedParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface Reader {

    Data readFile(File file);

    Stream<Data> readFiles(List<File> files);

    Stream<Data> readFilesParallel(List<File> files);

    Stream<Data> readFolder(File directory) throws ExgedParserException;

    Stream<Data> readFolderParallel(File directory) throws IOException, ExgedParserException;
}
