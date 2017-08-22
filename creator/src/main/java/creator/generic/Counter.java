package creator.generic;

import config.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;

@CreatorAnnotation(name = "counter")
public class Counter implements Creator {

    private static Map<String, Map<String, AtomicIntegerArray>> counterMap = new HashMap<>();

    @Override
    public void createValue(Fold fold, CreatorConfig creatorConfig) {
        fold.getData().forEach(row -> {
            counterMap.putIfAbsent(creatorConfig.getName(), new HashMap<>());
            counterMap.get(creatorConfig.getName()).putIfAbsent(row.get(fold.getHeader().get(creatorConfig.getName())),
                    new AtomicIntegerArray(creatorConfig.getHeaders().size()));
            final AtomicIntegerArray atomicIntegerArray = counterMap.get(creatorConfig.getName()).get(row.get(fold.getHeader().get(creatorConfig.getName())));
            // Pour commencer le compteur à 0
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                row.add(Integer.toString(atomicIntegerArray.get(i)));
                fold.getHeader().put(creatorConfig.getHeaders().get(i), row.size() - 1);
            }

            // Mise à jour des counter
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                if (atomicIntegerArray.get(i) < Integer.parseInt(creatorConfig.getArguments().get(i))) {
                    atomicIntegerArray.addAndGet(i, 1);
                    break;
                } else {
                    atomicIntegerArray.set(i, 0);
                }
            }
        });
    }
}
