package creator;

import data.Fold;
import reader.config.mapping.creators.CreatorConfig;

@FunctionalInterface
public interface Creator {
    void createValue(Fold fold, CreatorConfig creatorConfig);
}
