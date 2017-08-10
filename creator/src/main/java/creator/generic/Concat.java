package creator.generic;

import config.json.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.util.StringJoiner;

@CreatorAnnotation(name = "concat")
public class Concat implements Creator {

    @Override
    public void createValue(Fold fold, CreatorConfig creatorConfig) {
        fold.getData().forEach(row -> {
            StringJoiner stringJoiner = new StringJoiner(creatorConfig.getArguments().get(0));
            creatorConfig.getHeaders().forEach(header -> {
                if (fold.getHeader().containsKey(header)) {
                    stringJoiner.add(row.get(fold.getHeader().get(header)));
                } else {
                    stringJoiner.add(header);
                }
            });
            row.add(stringJoiner.toString());
        });
        fold.getHeader().put(creatorConfig.getName(), fold.getData().get(0).size() - 1);
    }
}
