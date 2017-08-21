package convertor;

import config.GenericNameWithArguments;
import data.Fold;

@FunctionalInterface
public interface Converter {
    Fold convertHeader(Fold fold, GenericNameWithArguments complexConvertor, int headerIndex);
}
