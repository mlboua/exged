package creator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;
import org.reflections.Reflections;

import config.mapping.creators.CreatorConfig;
import data.Fold;

public class GenericCreator {

    private static final Map<String, Creator> creatorMap;

    static {
        creatorMap = new Reflections("creator").getTypesAnnotatedWith(CreatorAnnotation.class).stream()
                .collect(Collectors.toMap(GenericCreator::getCreatorName, GenericCreator::createCreatorInstance));

    }

    private final List<CreatorConfig> creatorConfigList;

    public GenericCreator(final List<CreatorConfig> creatorConfigList) {
        this.creatorConfigList = creatorConfigList;
    }

    private static String getCreatorName(final Class<?> creator) {
        return creator.getAnnotation(CreatorAnnotation.class).name();
    }

    private static Creator createCreatorInstance(final Class<?> creator) {
        try {
            return (Creator) creator.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException
                | IllegalAccessException e) {
            Logger.error("Problème lors de la création d'un créateur: " + e);
        }
        return null;
    }

    public void createFields(final Fold fold) {
        creatorConfigList.forEach(creator -> creatorMap.get(creator.getCreator()).createValue(fold, creator));
    }
}
