package validator.complex.validators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import data.Fold;
import exception.ExgedValidatorException;
import validator.Reject;
import validator.ValidatorAnnotation;
import validator.complex.ComplexValidationCondition;
import validator.complex.ComplexValidator;

@ValidatorAnnotation(name = "fileContent", type = "complex")
public class FileContent implements ComplexValidationCondition {

    private static Map<String, List<String>> mapper;

    static {
        mapper = new HashMap<>();
    }

    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold,
            final List<ComplexValidator> complexValidatorList, final Map<String, Integer> headers)
            throws ExgedValidatorException {
        final List<String> collect = fold.getData().stream()
                .map(row -> complexValidatorList.stream()
                        .filter(complexValidator -> !isInFile(complexValidator.getArguments().get(0),
                                row.get(fold.getHeader().get(complexValidator.getName())))) // Condition princpale
                        .map(complexValidator -> complexValidator.getName() + " - Line " + fold.getData().indexOf(row))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }

    private static void fileToList(final String path) {
        final List<String> values = new ArrayList<>();
        try {
            Files.lines(Paths.get(path)).distinct().map(line -> line.split(","))
                    .forEach(elt -> values.addAll(Arrays.asList(elt)));
            mapper.put(path, values);
        } catch (final IOException e) {
            Logger.info("Erreur lors de la lecture du fichier : " + path);
        }
    }

    private static boolean isInFile(final String path, final String toCheck) {
        if (!mapper.containsKey(path)) {
            fileToList(path);
        }
        return mapper.get(path).contains(toCheck);
    }
}
