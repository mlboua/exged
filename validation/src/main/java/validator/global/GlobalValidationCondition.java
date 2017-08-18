package validator.global;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface GlobalValidationCondition {
    Optional<Reject> validate(final String rejectCode, final Fold fold, final List<GlobalValidator> globalValidatorList, final Map<String, Integer> headers) throws ExgedValidatorException;
}
