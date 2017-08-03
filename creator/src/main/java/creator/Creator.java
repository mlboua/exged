package creator;

import config.json.mapping.creator.MappingCreator;
import data.Fold;

import java.util.Map;

@FunctionalInterface
public interface Creator {
    Fold createValue(Fold fold, MappingCreator mappingCreator, Map<String, Integer> headers);
}
