package data;

import exception.ExgedCoreException;

import java.util.List;
import java.util.stream.Stream;

public interface Data {

    public Stream<List<String>> stream();

    public Stream<Fold> foldStream();
}
