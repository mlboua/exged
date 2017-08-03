package creator.generic;

import config.json.mapping.creator.MappingCreator;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.util.Map;

@CreatorAnnotation(name = "subString")
public class SubString implements Creator {
    @Override
    public Fold createValue(Fold fold, MappingCreator mappingCreator, Map<String, Integer> headers) {
        if (headers.get(mappingCreator.getHeaders().get(0)) != null) {
            fold.getData().stream().forEach(row -> {
                if (!"".equals(row.get(headers.get(mappingCreator.getHeaders().get(0))))) {
                    row.add(
                            row.get(headers.get(mappingCreator.getHeaders().get(0)))
                                    .substring(
                                            toInt(mappingCreator.getArguments().get(0)),
                                            toInt(mappingCreator.getArguments().get(1))
                                    )
                    );
                }
            });
            headers.put(mappingCreator.getName(), fold.getData().get(0).size() - 1);
        }
        return fold;
    }

    private int toInt(String string) {
        return Integer.parseInt(string);
    }
}
