package validator.complex.validators;

import data.Fold;
import exception.ExgedValidatorException;
import validator.complex.ComplexValidationCondition;
import validator.Reject;
import validator.ValidatorAnnotation;
import validator.complex.ComplexValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ValidatorAnnotation(name = "notEgalTo")
public class NotEgalTo implements ComplexValidationCondition {

    @Override
    public Optional<Reject> validate(String rejectCode, Fold fold, List<ComplexValidator> complexValidatorList, Map<String, Integer> headers) throws ExgedValidatorException {
        if (complexValidatorList.stream().anyMatch(complexValidatorArgs -> !complexValidatorArgs.getArguments().isPresent())) {
            throw new ExgedValidatorException("Un validateur complexe n'as aucun argument");
        }
        final List<String> collect = fold.getData().stream()
                .map(row -> complexValidatorList.stream()
                        .filter(complexValidator -> complexValidator.getArguments() .get() // Optionnel vérifié au début de la fonction
                                .stream()
                                .allMatch(value -> row.get(headers.get(complexValidator.getName())).equals(value)))    // Condition princpale
                        .map(complexValidator -> complexValidator.getName() + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
