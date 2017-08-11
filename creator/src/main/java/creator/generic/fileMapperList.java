package creator.generic;

import config.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@CreatorAnnotation(name = "fileMapperList")
public class fileMapperList implements Creator {

    private final static Map<String, Map<String, List<String>>> mapperFile;

    static {
        mapperFile = new HashMap<>();
    }

    private static boolean fileNotGenerated(CreatorConfig creatorConfig) {
        return !mapperFile.containsKey(creatorConfig.getArguments().get(0));
    }

    private static void generateMap(CreatorConfig creatorConfig) {
        try {
            Map<String, List<String>> converter = new HashMap<>();
            Files.lines(Paths.get(creatorConfig.getArguments().get(0)))
                    .map(line -> line.split(creatorConfig.getArguments().get(1)))
                    .forEach(line -> {
                        List<String> values = new ArrayList<>(Arrays.asList(line));
                        values.remove(0);
                        converter.put(line[0], values);
                    });
            mapperFile.put(creatorConfig.getArguments().get(0), converter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(List<CreatorConfig> creatorConfig) {
        creatorConfig.stream()
                .filter(fileMapperList::fileNotGenerated)
                .forEach(fileMapperList::generateMap);
    }

    @Override
    public void createValue(Fold fold, CreatorConfig creatorConfig) {
        if (!mapperFile.containsKey(creatorConfig.getArguments().get(0))) {
            init(Arrays.asList(creatorConfig));
        }
        String valueToAdd = mapperFile.get(creatorConfig.getArguments().get(0))                    // Map du fichier
                .get(fold.getData().get(0)                          // Get de la valeur dans le pli à l'index du header
                        .get(fold.getHeader().get(
                                creatorConfig.getHeaders().get(0))
                        )
                )
                .get(Integer.parseInt(creatorConfig.getArguments().get(2)));   // Get de la valeur demandé dans le resultat de la map
        fold.getData().forEach(row -> row.add(valueToAdd));
        fold.getHeader().put(creatorConfig.getName(), fold.getData().get(0).indexOf(valueToAdd));
    }
}
