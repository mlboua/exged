package validator;

import data.Fold;

import java.util.Optional;

@FunctionalInterface
public interface Validator {
    Optional<Reject> validateFold(Fold fold);
}
