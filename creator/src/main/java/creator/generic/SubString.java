package creator.generic;

import config.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

@CreatorAnnotation(name = "subString")
/**
 * Get substring from a string with borders indicated in creators.json/yaml file
 * return the entire string if the end index is greater than the string length
 * return empty string if begin index is greater than the string length
 *
 */
public class SubString implements Creator {
    @Override
    public void createValue(final Fold fold, final CreatorConfig creatorConfig) {

        if (fold.getHeader().get(creatorConfig.getHeaders().get(0)) != null) {
            fold.getData().forEach(row -> {
                final String field = row.get(fold.getHeader().get(creatorConfig.getHeaders().get(0)));
                final int beginInd = toInt(creatorConfig.getArguments().get(0));
                final int endInd = field.length() <= toInt(creatorConfig.getArguments().get(1)) ? field.length()
                        : toInt(creatorConfig.getArguments().get(1));
                if (!"".equals(field) & field.length() >= beginInd) {
                    row.add(field.substring(beginInd, endInd));
                } else {
                    row.add("");
                }
            });
            fold.getHeader().put(creatorConfig.getName(), fold.getData().get(0).size() - 1);
        }
    }

    private int toInt(final String string) {
        return Integer.parseInt(string);
    }
}
