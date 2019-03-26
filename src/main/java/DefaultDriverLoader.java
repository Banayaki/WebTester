import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class DefaultDriverLoader implements DriverLoader {

    private List<String> supportedBrowserList = Arrays.asList(
            "chrome"
    );

    public DefaultDriverLoader() {
        setupEnvironment();
    }

    public boolean isSupportedBrowser(String browserName) {
        return supportedBrowserList.contains(browserName.toLowerCase());
    }

    public WebDriver getWebDriverFor(String browserName) {
        if (!isSupportedBrowser(browserName)) {
            throw new IllegalArgumentException(browserName + "is unknown browser");
        }
        return createWebDriver(browserName);
    }

    private void setupEnvironment() {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver");
    }

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

    private String firstUpperCase(String str) {
        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("Empty string");
        return str
                .substring(0, 1)
                .toUpperCase()
                .concat(str.substring(1));
    }

    private WebDriver getChrome() {
        return new ChromeDriver();
    }
}
