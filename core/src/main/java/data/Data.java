package data;

import java.util.List;
import java.util.stream.Stream;

public interface Data {

    Stream<List<String>> stream();

    Stream<Fold> foldStream();
}
