package convertor;

import config.json.GenericNameWithArguments;
import data.Fold;

@FunctionalInterface
public interface Converter {
    Fold convertHeader(Fold fold, GenericNameWithArguments complexConvertor, int headerIndex);
}
