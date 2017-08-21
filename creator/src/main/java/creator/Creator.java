package creator;

import config.mapping.creators.CreatorConfig;
import data.Fold;

@FunctionalInterface
public interface Creator {
    void createValue(Fold fold, CreatorConfig creatorConfig);
}
