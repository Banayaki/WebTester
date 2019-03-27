import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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

    public DefaultDriverLoader() {
        setupEnvironment();
    }

    /**
     * Проверяет поддерживается ли данный браузер
     * @param browserName - название браузера
     * @return - true если браузер поддерживается
     */
    public boolean isSupportedBrowser(String browserName) {
        return supportedBrowserList.contains(browserName.toLowerCase());
    }

    /**
     * Метод возвращающий драйвер указанного браузера
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
     */
    private void setupEnvironment() {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver");
    }

    /**
     * Метод создающий WebDriver, внутри используется рефлексия, для поддержки добавления новых браузеров
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
