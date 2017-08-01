package validator.complex;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface ComplexValidationCondition {
    Optional<Reject> validate(final String rejectCode, final Fold fold, final List<ComplexValidator> complexValidatorList, final Map<String, Integer> headers) throws ExgedValidatorException;
}
