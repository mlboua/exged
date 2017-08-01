package data.csv;

import data.Fold;

import java.util.List;

public class CsvFold implements Fold {

    private final String id;
    private final List<List<String>> data;

    public CsvFold(String id, List<List<String>> data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public List<List<String>> getData() {
        return data;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        return "CsvFold{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
