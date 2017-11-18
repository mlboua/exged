package validator.multiHeader;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface MultiHeaderValidationCondition {
    Optional<Reject> validate(final String rejectCode, final Fold fold, final List<MultiHeadersValidator> multiHeadersValidatorList, final Map<String, Integer> headers) throws ExgedValidatorException;
}
