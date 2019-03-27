package parser;

import java.io.File;

public class ArgParser {

    /**
     * Путь до файла с конфигурацией тестирования
     */
    private String configPath;

    /**
     * @param args - аргументы командной строки
     */
    public ArgParser(String[] args) {
        parse(args);
    }

    /**
     * Метод проверяющие наличие аргументов, так же сохраняет путь в переменную
     * @param args - аргументы командной строки
     */
    private void parse(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Wrong launch parameters. You're entered: " + args.length);

        configPath = args[0];

        checkExist(configPath);
    }

    /**
     * Метод, проверяющий существование файла конфигурации
     * @param path - путь до файла конфигурации
     */
    private void checkExist(String path) {
        if (!new File(path).exists())
            throw new IllegalArgumentException(path + " File does not exist!");
    }

    public String getConfigPath() {
        return configPath;
    }
}
