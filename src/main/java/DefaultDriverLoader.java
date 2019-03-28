import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Стандартный загрузчик драйверов. Поддежривает только Google Chrome
 */
public class DefaultDriverLoader implements DriverLoader {

    /**
     * Список поддерживаемых опций (браузеров)
     */
    private List<String> supportedBrowserList = Arrays.asList(
            "chrome"
    );
//    private ChromeDriverService googleService;

    public DefaultDriverLoader() {
        setupEnvironment();
    }

    /**
     * Проверяет поддерживается ли данный браузер
     *
     * @param browserName - название браузера
     * @return - true если браузер поддерживается
     */
    public boolean isSupportedBrowser(String browserName) {
        return supportedBrowserList.contains(browserName.toLowerCase());
    }

    /**
     * Метод возвращающий драйвер указанного браузера
     *
     * @param browserName - запрашиваемый браузер
     * @return - драйвер указанного браузера
     */
    public WebDriver getWebDriverFor(String browserName) {
        if (!isSupportedBrowser(browserName)) {
            throw new IllegalArgumentException(browserName + "is unknown browser");
        }
        return createWebDriver(browserName);
    }

    /**
     * Устанавливает необходимые свойства (путь до драйвера)
     * Jar adaptation
     */
    private void setupEnvironment() {
        // native version
        // System.setProperty("webdriver.chrome.driver", "driver/chromedriver");
        try {
            InputStream stream = getClass().getResourceAsStream("/driver/chromedriver");

            File driverFile = File.createTempFile("chromedriver", "");
            driverFile.deleteOnExit();

            FileUtils.copyInputStreamToFile(stream, driverFile);

            if (!driverFile.canExecute())
                driverFile.setExecutable(true);

            System.setProperty("webdriver.chrome.driver", driverFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод создающий WebDriver, внутри используется рефлексия, для поддержки добавления новых браузеров
     *
     * @param name - название браузера
     * @return - WebDriver
     */
    private WebDriver createWebDriver(String name) {
        try {
            Method invokedMethod = this.getClass().getDeclaredMethod("get".concat(firstUpperCase(name)));
            return (WebDriver) invokedMethod.invoke(this);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Первую букву в upperCase
     *
     * @param str - входная строка
     * @return - входная строка, в которой первая буква - заглавная
     */
    private String firstUpperCase(String str) {
        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("Empty string");
        return str
                .substring(0, 1)
                .toUpperCase()
                .concat(str.substring(1));
    }

    /**
     * @return возвращает драйвер для google chrome
     */
    private WebDriver getChrome() {
        return new ChromeDriver();
    }
}
