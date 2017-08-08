package creator.generic;

import config.json.mapping.creator.MappingCreator;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

@CreatorAnnotation(name = "subString")
public class SubString implements Creator {
    @Override
    public void createValue(Fold fold, MappingCreator mappingCreator) {
        if (fold.getHeader().get(mappingCreator.getHeaders().get(0)) != null) {
            fold.getData().forEach(row -> {
                if (!"".equals(row.get(fold.getHeader().get(mappingCreator.getHeaders().get(0))))) {
                    row.add(
                            row.get(fold.getHeader().get(mappingCreator.getHeaders().get(0)))
                                    .substring(
                                            toInt(mappingCreator.getArguments().get(0)),
                                            toInt(mappingCreator.getArguments().get(1))
                                    )
                    );
                }
            });
            fold.getHeader().put(mappingCreator.getName(), fold.getData().get(0).size() - 1);
        }
    }

    private int toInt(String string) {
        return Integer.parseInt(string);
    }
}
