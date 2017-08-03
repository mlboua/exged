package creator.generic;

import config.json.mapping.creator.MappingCreator;
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

    private static boolean fileNotGenerated(MappingCreator mappingCreator) {
        return !mapperFile.containsKey(mappingCreator.getArguments().get(0));
    }

    private static void generateMap(MappingCreator mappingCreator) {
        try {
            Map<String, List<String>> converter = new HashMap<>();
            Files.lines(Paths.get(mappingCreator.getArguments().get(0)))
                    .map(line -> line.split(mappingCreator.getArguments().get(1)))
                    .forEach(line -> {
                        List<String> values = new ArrayList<>(Arrays.asList(line));
                        values.remove(0);
                        converter.put(line[0], values);
                    });
            mapperFile.put(mappingCreator.getArguments().get(0), converter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(List<MappingCreator> mappingCreator) {
        mappingCreator.stream()
                .filter(fileMapperList::fileNotGenerated)
                .forEach(fileMapperList::generateMap);
    }

    @Override
    public Fold createValue(Fold fold, MappingCreator mappingCreator, Map<String, Integer> headers) {
        if (!mapperFile.containsKey(mappingCreator.getArguments().get(0))) {
            init(Arrays.asList(mappingCreator));
        }
        String valueToAdd = mapperFile.get(mappingCreator.getArguments().get(0))                    // Map du fichier
                .get(fold.getData().get(0)                          // Get de la valeur dans le pli à l'index du header
                        .get(headers.get(
                                mappingCreator.getHeaders().get(0))
                        )
                )
                .get(Integer.parseInt(mappingCreator.getArguments().get(2)));   // Get de la valeur demandé dans le resultat de la map
        fold.getData().forEach(row -> row.add(valueToAdd));
        fold.getHeader().put(mappingCreator.getName(), fold.getData().get(0).indexOf(valueToAdd));
        return fold;
    }
}
