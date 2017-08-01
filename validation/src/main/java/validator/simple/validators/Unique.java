package validator.simple.validators;

import data.Fold;
import validator.Reject;
import validator.simple.SimpleValidationCondition;
import validator.ValidatorAnnotation;
import exception.ExgedValidatorException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ValidatorAnnotation(name = "unique")
public class Unique implements SimpleValidationCondition {

    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold, final List<String> headerValidation, final Map<String, Integer> headers) throws ExgedValidatorException {
        if (fold.getData().isEmpty()) {
            throw new ExgedValidatorException("Aucune donnée dans le plis: " + fold);
        }
        final List<String> collect = headerValidation.stream()
                .filter(header -> fold.getData().stream()
                        .map(row -> row.get(headers.get(header)))
                        .distinct()
                        .count() == 1)
                .collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}

