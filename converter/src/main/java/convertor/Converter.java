package convertor;

import data.Fold;
import reader.config.GenericNameWithArguments;

@FunctionalInterface
public interface Converter {
    Fold convertHeader(Fold fold, GenericNameWithArguments complexConvertor, int headerIndex);
}
