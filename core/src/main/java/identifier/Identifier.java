package identifier;

import java.util.List;
import java.util.Optional;

public class Identifier {

    protected String name;
    protected List<String> rejectedValues;
    protected Optional<String> replacedBy;

    public Identifier() {
    }

    public Identifier(String name, List<String> rejectedValues) {
        this.name = name;
        this.rejectedValues = rejectedValues;
        this.replacedBy = Optional.empty();
    }

    public Identifier(String name, List<String> rejectedValues, Optional<String> replacedBy) {
        this.name = name;
        this.rejectedValues = rejectedValues;
        this.replacedBy = replacedBy;
    }

    public boolean matchData(String data) {
        return rejectedValues.stream().noneMatch(data::equals);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRejectedValues() {
        return rejectedValues;
    }

    public void setRejectedValues(List<String> rejectedValues) {
        this.rejectedValues = rejectedValues;
    }

    public Optional<String> getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(Optional<String> replacedBy) {
        this.replacedBy = replacedBy;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "name='" + name + '\'' +
                ", rejectedValues=" + rejectedValues +
                ", replacedBy=" + replacedBy +
                '}';
    }
}
