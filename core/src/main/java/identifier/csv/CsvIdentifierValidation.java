package identifier.csv;

import java.util.List;
import java.util.Optional;

public class CsvIdentifierValidation implements CsvIdentifierValidationCondition {

    @Override
    public Optional<CsvIdentifier> validate(List<CsvIdentifier> identifiers, List<String> data) {
        return identifiers.stream().filter(identifier -> identifier.matchData(data.get(identifier.getIndex()))).findFirst();
    }
}
