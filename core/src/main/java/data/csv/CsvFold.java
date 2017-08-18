package data.csv;

import data.Fold;
import data.FoldStatus;

import java.util.List;
import java.util.Map;

public class CsvFold implements Fold {

    private String id;
    private final List<List<String>> data;
    private final Map<String, Integer> headers;
    private FoldStatus foldStatus;

    public CsvFold(String id, List<List<String>> data, Map<String, Integer> headers, FoldStatus foldStatus) {
        this.id = id;
        this.data = data;
        this.headers = headers;
        this.foldStatus = foldStatus;
    }

    @Override
    public List<List<String>> getData() {
        return data;
    }

    @Override
    public Map<String, Integer> getHeader() {
        return headers;
    }

    @Override
    public String getValue(int rowNumber, int rowIndex) {
        return data.get(rowNumber).get(rowIndex);
    }

    @Override
    public String getValue(int rowNumber, String indexName) {
        return data.get(rowNumber).get(headers.get(indexName));
    }

    @Override
    public FoldStatus getStatus() {
        return this.foldStatus;
    }

    @Override
    public void setStatus(FoldStatus foldStatus) {
        this.foldStatus = foldStatus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CsvFold{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
