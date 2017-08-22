package data;

import java.util.List;
import java.util.Map;

public interface Fold {

    String getId();
    void setId(String id);

    List<List<String>> getData();
    Map<String, Integer> getHeader();
    void setHeader(Map<String, Integer> headers);

    String getValue(int rowNumber, int rowIndex);

    String getValue(int rowNumber, String indexName);

    FoldStatus getStatus();

    void setStatus(FoldStatus foldStatus);
}
