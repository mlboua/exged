package creator.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;

import config.mapping.creators.CreatorConfig;
import creator.Creator;
import creator.CreatorAnnotation;
import data.Fold;

@CreatorAnnotation(name = "counter")
public class Counter implements Creator {

    private static Map<String, Map<String, AtomicIntegerArray>> counterMap = new HashMap<>();

    @Override
    public void createValue(final Fold fold, final CreatorConfig creatorConfig) {
        fold.getData().forEach(row -> {
            counterMap.putIfAbsent(creatorConfig.getName(), new HashMap<>());
            if (!counterMap.get(creatorConfig.getName())
                    .containsKey(row.get(fold.getHeader().get(creatorConfig.getName())))) {
                counterMap.get(creatorConfig.getName()).put(row.get(fold.getHeader().get(creatorConfig.getName())),
                        new AtomicIntegerArray(creatorConfig.getHeaders().size()));
                final AtomicIntegerArray atomicIntegerArraySet = counterMap.get(creatorConfig.getName())
                        .get(row.get(fold.getHeader().get(creatorConfig.getName())));
                final List<Integer> startCounterList = creatorConfig.getArguments().stream()
                        .map(argument -> argument.split("-")[0]).map(argument -> Integer.parseInt(argument))
                        .collect(Collectors.toList());
                for (int i = 0; i < creatorConfig.getArguments().size(); i++) {
                    atomicIntegerArraySet.set(i, startCounterList.get(i));
                }
            }

            //Recupère le nombre de digits sur le quel est representé le nombre
            final List<Integer> digitNumberList = creatorConfig.getArguments().stream()
                    .map(argument -> argument.split("-")[2]).map(argument -> Integer.parseInt(argument))
                    .collect(Collectors.toList());

            final AtomicIntegerArray atomicIntegerArray = counterMap.get(creatorConfig.getName())
                    .get(row.get(fold.getHeader().get(creatorConfig.getName())));
            // Pour commencer le compteur à 0
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                row.add(String.format("%0" + digitNumberList.get(i) + "d", atomicIntegerArray.get(i)));
                fold.getHeader().put(creatorConfig.getHeaders().get(i), row.size() - 1);
            }

            // Mise à jour des counter
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                if (atomicIntegerArray.get(i) < Integer.parseInt(creatorConfig.getArguments().get(i).split("-")[1])) {
                    atomicIntegerArray.addAndGet(i, 1);
                    break;
                } else {
                    atomicIntegerArray.set(i, 0);
                }
            }
        });
    }
}
