package creator;

import config.json.mapping.creator.MappingCreator;
import data.Fold;

@FunctionalInterface
public interface Creator {
    void createValue(Fold fold, MappingCreator mappingCreator);
}
