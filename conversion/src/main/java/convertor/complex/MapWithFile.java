package convertor.complex;

import config.json.GenericNameWithArguments;
import convertor.Converter;
import data.Fold;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWithFile implements Converter {

    private final static Map<String, Map<String, String>> mapperFile;

    static {
        mapperFile = new HashMap<>();
    }

    private static boolean fileNotGenerated(GenericNameWithArguments complexConverter) {
        return complexConverter.getArguments().isPresent() && !mapperFile.containsKey(complexConverter.getArguments().get().get(0));
    }

    private static void generateMap(GenericNameWithArguments complexConverter) {
        try {
            if (complexConverter.getArguments().isPresent()) {
                List<String> args = complexConverter.getArguments().get();
                int indexLeft = Integer.parseInt(args.get(2));
                int indexRight = Integer.parseInt(args.get(3));
                Map<String, String> converter = new HashMap<>();
                Files.lines(Paths.get(args.get(0)))
                        .map(line -> line.split(args.get(1)))
                        .forEach(line -> {
                            converter.put(line[indexLeft], line[indexRight]);
                        });
                mapperFile.put(args.get(0), converter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(List<GenericNameWithArguments> complexConverterList) {
        complexConverterList.stream()
                .filter(MapWithFile::fileNotGenerated)
                .forEach(MapWithFile::generateMap);
    }

    @Override
    public Fold convertHeader(Fold fold, GenericNameWithArguments complexConverter, int headerIndex) {
        fold.getData().forEach(row -> complexConverter.getArguments()
                .ifPresent(complexConverterArgs ->
                        row.set(headerIndex, mapperFile.get(complexConverterArgs.get(0))
                                .get(row.get(headerIndex)))));
        return fold;
    }
}
