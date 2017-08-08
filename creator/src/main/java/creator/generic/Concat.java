package creator.generic;

import config.json.mapping.creator.MappingCreator;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.util.StringJoiner;

@CreatorAnnotation(name = "concat")
public class Concat implements Creator {

    @Override
    public void createValue(Fold fold, MappingCreator mappingCreator) {
        fold.getData().forEach(row -> {
            StringJoiner stringJoiner = new StringJoiner(mappingCreator.getArguments().get(0));
            mappingCreator.getHeaders().forEach(header -> {
                if (fold.getHeader().containsKey(header)) {
                    stringJoiner.add(row.get(fold.getHeader().get(header)));
                } else {
                    stringJoiner.add(header);
                }
            });
            row.add(stringJoiner.toString());
        });
        fold.getHeader().put(mappingCreator.getName(), fold.getData().get(0).size() - 1);
    }
}
