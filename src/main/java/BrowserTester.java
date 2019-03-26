import parser.ArgParser;

/**
 * Класс, запускающий тестирование.
 */
public class BrowserTester {

    /**
     * @param args - принимает аргументы командной строки. Ожидается: args[0] - название бразуера,
     *             args[1] - путь до конфига теста
     */
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);
        DriverLoader driverLoader = new DefaultDriverLoader();

        driverLoader.getWebDriverFor(argParser.getBrowserName());
    }

}
