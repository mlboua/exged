package creator.generic;

import config.json.mapping.creator.MappingCreator;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.util.Map;
import java.util.StringJoiner;

@CreatorAnnotation(name = "concat")
public class Concat implements Creator {

    @Override
    public Fold createValue(Fold fold, MappingCreator mappingCreator, Map<String, Integer> headers) {
        fold.getData().forEach(row -> {
            StringJoiner stringJoiner = new StringJoiner(mappingCreator.getArguments().get(0));
            mappingCreator.getHeaders().forEach(header -> {
                if (headers.containsKey(header)) {
                    stringJoiner.add(row.get(headers.get(header)));
                } else {
                    stringJoiner.add(header);
                }
            });
            row.add(stringJoiner.toString());
        });
        headers.put(mappingCreator.getName(), fold.getData().get(0).size() - 1);
        return fold;
    }
}
