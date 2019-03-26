import org.openqa.selenium.WebDriver;

public interface DriverLoader {

    boolean isSupportedBrowser(String browserName);

    WebDriver getWebDriverFor(String browserName);
}
