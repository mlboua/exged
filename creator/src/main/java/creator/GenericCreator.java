package creator;

import config.mapping.creators.CreatorConfig;
import data.Fold;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericCreator {

    private static final Map<String, Creator> creatorMap;

    static {
        Reflections ref = new Reflections("creator");
        creatorMap = ref.getTypesAnnotatedWith(CreatorAnnotation.class)
                .stream()
                .collect(Collectors.toMap(GenericCreator::getCreatorName, GenericCreator::createCreatorInstance));

    }

    private final List<CreatorConfig> creatorConfigList;

    public GenericCreator(List<CreatorConfig> creatorConfigList) {
        this.creatorConfigList = creatorConfigList;
    }

    private static String getCreatorName(Class<?> creator) {
        return creator.getAnnotation(CreatorAnnotation.class).name();
    }

    private static Creator createCreatorInstance(Class<?> creator) {
        try {
            return (Creator) creator.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Fold createFields(Fold fold, Map<String, Integer> headers) {
        creatorConfigList.forEach(mappingCreator -> {
            if (creatorMap.containsKey(mappingCreator.getCreator())) {
                creatorMap.get(mappingCreator.getCreator()).createValue(fold, mappingCreator);
            } else {
                System.out.println(mappingCreator.getCreator());
            }
        });
        return fold;
    }
}
