package validator.simple.validators;

import data.Fold;
import validator.Reject;
import validator.simple.SimpleValidationCondition;
import validator.ValidatorAnnotation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ValidatorAnnotation(name = "notNull")
public class notNull implements SimpleValidationCondition {

    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold, final List<String> headerValidation, final Map<String, Integer> headers) {
        final List<String> collect = fold.getData().stream()
                .map(row -> headerValidation.stream()
                        .filter(header -> row.get(headers.get(header)) == null)     // Condition princpale
                        .map(header -> header + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
