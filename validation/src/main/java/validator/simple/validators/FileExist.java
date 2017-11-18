package validator.simple.validators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import config.Config;
import data.Fold;
import validator.Reject;
import validator.ValidatorAnnotation;
import validator.simple.SimpleValidationCondition;

@ValidatorAnnotation(name = "fileExist", type = "simple")
public class FileExist implements SimpleValidationCondition {

    @Override
    public Optional<Reject> validate(final String rejectCode, final Fold fold, final List<String> headerValidation,
            final Map<String, Integer> headers) {
        final List<String> collect = fold.getData().stream()
                .map(row -> headerValidation.stream().filter(header -> !Files.exists(Paths.get(FilenameUtils.normalize(
                        Config.getMainConfig().getExternFilesPath() + File.separator + row.get(headers.get(header)))))) // Condition princpale
                        .map(header -> header + " - Line " + fold.getData().indexOf(row)).collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList());
        return collect.isEmpty() ? Optional.empty() : Optional.of(new Reject(rejectCode, collect));
    }
}
