package parser;

import java.io.File;

public class ArgParser {

    private String configPath;

    public ArgParser(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Wrong launch parameters. You're entered: " + args.length);

        configPath = args[0];

        checkExist(configPath);
    }

    private void checkExist(String path) {
        if (!new File(path).exists())
            throw new IllegalArgumentException(path + " File does not exist!");
    }

    public String getConfigPath() {
        return configPath;
    }
}
