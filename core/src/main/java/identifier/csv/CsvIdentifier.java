package identifier.csv;

import identifier.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CsvIdentifier extends Identifier {

    private int index;

    public CsvIdentifier(String name, List<String> rejectedValues, int index) {
        super(name, rejectedValues);
        this.index = index;
    }

    public CsvIdentifier(String name, List<String> rejectedValues, Optional<String> replacedBy, int index) {
        super(name, rejectedValues, replacedBy);
        this.index = index;
    }

    public CsvIdentifier(Identifier identifier) {
        super(identifier.getName(), identifier.getRejectedValues(), identifier.getReplacedBy());
    }

    public boolean detectIdentifierIndex(Map<String, Integer> headers) {
        if(headers.containsKey(this.getName())) {
            return false;
        }
        this.index = headers.get(this.getName());
        return true;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "CsvIdentifier{ " +
                "name='" + name + '\'' +
                ", rejectedValues=" + rejectedValues +
                ", replacedBy=" + replacedBy +
                ", index=" + index +
                " }";
    }
}
