package identifier.csv;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface CsvIdentifierValidationCondition {
    public Optional<CsvIdentifier> validate(final List<CsvIdentifier> identifiers, List<String> data);
}
