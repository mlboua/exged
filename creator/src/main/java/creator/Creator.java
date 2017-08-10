package creator;

import config.json.mapping.creators.CreatorConfig;
import data.Fold;

@FunctionalInterface
public interface Creator {
    void createValue(Fold fold, CreatorConfig creatorConfig);
}
