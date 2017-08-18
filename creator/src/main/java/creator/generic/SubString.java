package creator.generic;

import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;
import reader.config.mapping.creators.CreatorConfig;

@CreatorAnnotation(name = "subString")
public class SubString implements Creator {
    @Override
    public void createValue(Fold fold, CreatorConfig creatorConfig) {
        if (fold.getHeader().get(creatorConfig.getHeaders().get(0)) != null) {
            fold.getData().forEach(row -> {
                if (!"".equals(row.get(fold.getHeader().get(creatorConfig.getHeaders().get(0))))) {
                    row.add(
                            row.get(fold.getHeader().get(creatorConfig.getHeaders().get(0)))
                                    .substring(
                                            toInt(creatorConfig.getArguments().get(0)),
                                            toInt(creatorConfig.getArguments().get(1))
                                    )
                    );
                }
            });
            fold.getHeader().put(creatorConfig.getName(), fold.getData().get(0).size() - 1);
        }
    }

    private int toInt(String string) {
        return Integer.parseInt(string);
    }
}
