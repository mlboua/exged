package validator.complex.validators;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;
import validator.ValidatorAnnotation;
import validator.complex.ComplexValidationCondition;
import validator.complex.ComplexValidator;

@ValidatorAnnotation(name = "minLength", type = "complex")
public class MinLength implements ComplexValidationCondition {

    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold,
            final List<ComplexValidator> complexValidatorList, final Map<String, Integer> headers)
            throws ExgedValidatorException {
        final List<String> collect = fold.getData().stream()
                .map(row -> complexValidatorList.stream()
                        .filter(complexValidator -> row.get(fold.getHeader().get(complexValidator.getName()))
                                .length() < Integer.parseInt(complexValidator.getArguments().get(1))) // Condition princpale
                        .map(complexValidator -> complexValidator.getName() + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
