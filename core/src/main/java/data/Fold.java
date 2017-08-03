package data;

import java.util.List;
import java.util.Map;

public interface Fold {

    String getId();

    List<List<String>> getData();

    Map<String, Integer> getHeader();
}
