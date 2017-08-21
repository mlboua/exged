package creator;

import config.mapping.creators.CreatorConfig;
import data.Fold;
import org.pmw.tinylog.Logger;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericCreator {

    private static final Map<String, Creator> creatorMap;

    static {
        creatorMap = new Reflections("creator")
                .getTypesAnnotatedWith(CreatorAnnotation.class)
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
            Logger.error("Problème lors de la création d'un créateur: " + e);
        }
        return null;
    }

    public void createFields(Fold fold) {
        creatorConfigList.forEach(creator -> creatorMap.get(creator.getCreator()).createValue(fold, creator));
    }
}
