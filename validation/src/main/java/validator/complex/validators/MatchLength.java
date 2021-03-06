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

@ValidatorAnnotation(name = "matchLength", type = "complex")
public class MatchLength implements ComplexValidationCondition {
    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold,
            final List<ComplexValidator> complexValidatorList, final Map<String, Integer> headers)
            throws ExgedValidatorException {
        //System.out.println(fold.getData());
        final List<String> collect = fold.getData().stream()
                .map(row -> complexValidatorList.stream().filter(complexValidator -> {
                    if (fold.getHeader().get(complexValidator.getName()) == null
                            || row.get(fold.getHeader().get(complexValidator.getName())) == null
                            || "".equals(row.get(fold.getHeader().get(complexValidator.getName())))) {
                        return false;
                    } else {
                        return row.get(fold.getHeader().get(complexValidator.getName())).length() != // Condition princpale
                        Integer.parseInt(complexValidator.getArguments().get(0));
                    }
                }).map(complexValidator -> complexValidator.getName() + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
