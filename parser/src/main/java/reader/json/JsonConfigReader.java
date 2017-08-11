package reader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import config.mapping.creators.CreatorConfig;
import config.mapping.reject.RejectConfig;
import config.mapping.validations.ValidatorsConfig;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonConfigReader {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }


    private JsonConfigReader() {
    }

    public static ValidatorsConfig readJsonMappingHeaders(File file) throws IOException {
        return mapper.readValue(file, ValidatorsConfig.class);
    }

    public static List<RejectConfig> readJsonMappingReject(File file) throws IOException {
        return Arrays.asList(mapper.readValue(file, RejectConfig[].class));
    }

    public static List<CreatorConfig> readJsonMappingCreator(File file) throws IOException {
        return Arrays.asList(mapper.readValue(file, CreatorConfig[].class));
    }
}
