package engine;

import config.mapping.mainconfig.MainConfig;
import org.rythmengine.Rythm;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.resource.StringTemplateResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TemplateEngineExecutor {

    private final TemplateClass templateClass;
    private final List<RythmEngineStatus> rythmEngineList;

    /**
     * @param mainConfig
     */
    public TemplateEngineExecutor(final MainConfig mainConfig) {
        Rythm.engine().classes().clear();

        templateClass = Rythm.engine().getTemplateClass(new StringTemplateResource(
                readLineByLine(mainConfig.getTemplateFolder() + File.separator + mainConfig.getMigration() + ".template")));
        rythmEngineList = new ArrayList<>();
        IntStream.range(0, 20).forEach(key -> rythmEngineList.add(new RythmEngineStatus()));
    }

    private String readLineByLine(final String filePath) {
        final StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (final IOException e) {
            System.err.println(e);
        }

        return contentBuilder.toString();
    }

    public Optional<String> render(final Map<String, Object> params) {
        Optional<RythmEngineStatus> engine = rythmEngineList
                .stream()
                .filter(RythmEngineStatus::isAvailable)
                .findFirst();
        while (!engine.isPresent()) {
            engine = rythmEngineList.stream().filter(RythmEngineStatus::isAvailable).findFirst();
        }
        return Optional.of(engine.get().render(params, templateClass));
    }
}
