package data.csv;

import data.Fold;

import java.util.List;
import java.util.Map;

public class CsvFold implements Fold {

    private String id;
    private final List<List<String>> data;
    private final Map<String, Integer> headers;

    public CsvFold(String id, List<List<String>> data, Map<String, Integer> headers) {
        this.id = id;
        this.data = data;
        this.headers = headers;
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
