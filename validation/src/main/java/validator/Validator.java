package validator;

import data.Fold;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface Validator {
    Optional<List<DetailReject>> validateFold(Fold fold);
}
