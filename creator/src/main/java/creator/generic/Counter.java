package creator.generic;

import config.json.mapping.creator.MappingCreator;
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
    public void createValue(Fold fold, MappingCreator mappingCreator) {
        fold.getData().forEach(row -> {
            counterMap.putIfAbsent(mappingCreator.getName(), new HashMap<>());
            counterMap.get(mappingCreator.getName()).putIfAbsent(row.get(fold.getHeader().get(mappingCreator.getName())),
                    new AtomicIntegerArray(mappingCreator.getHeaders().size()));
            final AtomicIntegerArray atomicIntegerArray = counterMap.get(mappingCreator.getName()).get(row.get(fold.getHeader().get(mappingCreator.getName())));
            // Pour commencer le compteur à 0
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                row.add(Integer.toString(atomicIntegerArray.get(i)));
                fold.getHeader().put(mappingCreator.getHeaders().get(i), fold.getData().get(0).size() - 1);
            }

            // Mise à jour des counter
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                if (atomicIntegerArray.get(i) < Integer.parseInt(mappingCreator.getArguments().get(i))) {
                    atomicIntegerArray.addAndGet(i, 1);
                    break;
                } else {
                    atomicIntegerArray.set(i, 0);
                }
            }
        });
    }
}
