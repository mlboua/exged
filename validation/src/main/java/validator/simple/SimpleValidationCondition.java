package validator.simple;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface SimpleValidationCondition {
    Optional<Reject> validate(final String rejectCode, final Fold fold, final List<String> headerValidation, final Map<String, Integer> headers) throws ExgedValidatorException;
}
