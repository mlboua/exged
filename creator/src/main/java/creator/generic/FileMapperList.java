package creator.generic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;

import config.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

@CreatorAnnotation(name = "fileMapperList")
public class FileMapperList implements Creator {

    private static final Map<String, Map<String, List<String>>> mapperFile;

    static {
        mapperFile = new HashMap<>();
    }

    private static boolean fileNotGenerated(final CreatorConfig creatorConfig) {
        return !mapperFile.containsKey(creatorConfig.getArguments().get(0));
    }

    private static void generateMap(final CreatorConfig creatorConfig) {
        try {
            final Map<String, List<String>> converter = new HashMap<>();
            Files.lines(Paths.get(creatorConfig.getArguments().get(0)))
                    .map(line -> line.split(creatorConfig.getArguments().get(1))).forEach(line -> {
                        final List<String> values = new ArrayList<>(Arrays.asList(line));
                        values.remove(0);
                        converter.put(line[0], values);
                    });
            mapperFile.put(creatorConfig.getArguments().get(0), converter);
        } catch (final IOException e) {
            Logger.error(e);
        }
    }

    private static void init(final List<CreatorConfig> creatorConfig) {
        creatorConfig.stream().filter(FileMapperList::fileNotGenerated).forEach(FileMapperList::generateMap);
    }

    @Override
    public void createValue(final Fold fold, final CreatorConfig creatorConfig) {
        if (!mapperFile.containsKey(creatorConfig.getArguments().get(0))) {
            init(Collections.singletonList(creatorConfig));
        }
        final String valueToAdd = mapperFile.get(creatorConfig.getArguments().get(0)) // Map du fichier
                .get(fold.getData().get(0) // Get de la valeur dans le pli à l'index du header
                        .get(fold.getHeader().get(creatorConfig.getHeaders().get(0))))
                .get(Integer.parseInt(creatorConfig.getArguments().get(2))); // Get de la valeur demandé dans le resultat de la map

        if (!Strings.isNullOrEmpty(valueToAdd)) {
            fold.getData().forEach(row -> row.add(valueToAdd));
        }
        fold.getHeader().put(creatorConfig.getName(), fold.getData().get(0).size() - 1);

    }
}
