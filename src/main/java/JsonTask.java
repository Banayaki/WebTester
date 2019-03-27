import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class JsonTask {

    private Map options;
    private JSONArray task;
    private DriverLoader driverLoader;
    private WebDriverWait driverWait;
    private WebDriver driver;
    private WebElement currentElement;

    public JsonTask(String configPath, DriverLoader driverLoader) {
        this.driverLoader = driverLoader;
        createTaskFromFile(configPath);
    }

    public void createTaskFromFile(String path) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));


            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);


            JSONObject json = new JSONObject(stringBuilder.toString());

            if (!json.has("option") || !json.has("task"))
                throw new JSONException("Incorrect json format");

            task = json.getJSONArray("task");

            options = parseOption(json.getJSONObject("option"));

        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void solveTask() {
        if (task == null)
            throw new NullPointerException("Test configuration not loaded");

        driver = driverLoader.getWebDriverFor((String) options.get("browser"));
        if (options.containsKey("wait"))
            driverWait = new WebDriverWait(driver, (Long) options.get("time"));
        else
            driverWait = new WebDriverWait(driver, 10);

        parseTask();
        driver.quit();
    }

    private void parseTask() {
        for (Object o : task) {
            JSONObject json = (JSONObject) o;
            String action = json.getString("action");
            doAction(action, json);
        }
    }

    private Map parseOption(JSONObject option) {
        return option.toMap();
    }

    private void doClick(JSONObject json) {
        String findMethod = json.getString("type");
        String target = json.getString("target");

        if (findMethod.equals("xpath"))
            driver.findElement(By.xpath(target)).click();

    }

    private void doSetValue(JSONObject json) {
        String findMethod = json.getString("type");
        String target = json.getString("target");
        String value = json.getString("value");

        if (findMethod.equals("xpath"))
            driver.findElement(By.xpath(target)).sendKeys(value);
    }

    private void doOpenUrl(JSONObject json) {
        driver.get(json.getString("url"));
    }

    private void doScreenshot(JSONObject json) {
        File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(tmp, new File(json.getString("fileName").concat(".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doCheckElementVisible(JSONObject json) {

    }

    private void doAction(String action, JSONObject obj) {
        try {
            Method invokedMethod = this.getClass().getDeclaredMethod("do".concat(firstUpperCase(action)), obj.getClass());
            invokedMethod.invoke(this, obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private String firstUpperCase(String str) {
        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("Empty string");
        return str
                .substring(0, 1)
                .toUpperCase()
                .concat(str.substring(1));
    }

}
