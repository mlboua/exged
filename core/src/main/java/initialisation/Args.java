package initialisation;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = {
            "--reader.config",
            "-c"}, description = "Emplacement du fichier de configuration")
    private String configPath;

    /**
     * @return the configPath
     */
    public final synchronized String getConfigPath() {
        return configPath;
    }

}
