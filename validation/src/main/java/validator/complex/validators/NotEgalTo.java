package validator.complex.validators;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;
import validator.ValidatorAnnotation;
import validator.complex.ComplexValidationCondition;
import validator.complex.ComplexValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ValidatorAnnotation(name = "notEgalTo", type = "complex")
public class NotEgalTo implements ComplexValidationCondition {

    @Override
    public Optional<Reject> validate(String rejectCode, Fold fold, List<ComplexValidator> complexValidatorList, Map<String, Integer> headers) throws ExgedValidatorException {
        final List<String> collect = fold.getData().stream()
                .map(row -> complexValidatorList.stream()
                        .filter(complexValidator -> complexValidator.getArguments() // Optionnel vérifié au début de la fonction
                                .stream()
                                .anyMatch(value -> {
                                    if (headers.get(complexValidator.getName()) == null ||
                                            row.get(headers.get(complexValidator.getName())) == null ||
                                            "".equals(row.get(headers.get(complexValidator.getName())))) {
                                        return false;
                                    }
                                    return row.get(headers.get(complexValidator.getName())).equals(value);
                                }))
                        .map(complexValidator -> complexValidator.getName() + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
