package reader.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import config.mapping.mainconfig.MainConfig;

import java.io.File;
import java.io.IOException;

public class YamlConfigReader {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper(new YAMLFactory())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    public static MainConfig readYamlConfig(File file) throws IOException {
        return mapper.readValue(file, MainConfig.class);
    }
}
