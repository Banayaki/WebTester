package parser;

import java.io.File;

public class ArgParser {

    private String browserName;
    private String configPath;

    public ArgParser(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Wrong launch parameters");

        browserName = args[0];
        configPath = args[1];

        checkExist(configPath);
    }

    private void checkExist(String path) {
        if (!new File(path).exists())
            throw new IllegalArgumentException(path + " File does not exist!");
    }

    public String getBrowserName() {
        return browserName;
    }

    public String getConfigPath() {
        return configPath;
    }
}
