import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Класс выполняющий тестирование следуя конфигурации указанной в json-файле
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonTask {

    /**
     * Карта с опциями запуска теста (на данный момент используется для познания, какой браузер используем
     */
    private Map options;
    /**
     * Загруженная в память конфигурация теста
     */
    private JSONArray task;
    /**
     * Загрузчик WebDriver
     */
    private DriverLoader driverLoader;
    /**
     * Драйвер целевого браузера
     */
    private WebDriver driver;

    /**
     * @param configPath   - путь до конфигурации тестирования
     * @param driverLoader - объект - загрузчик драйвера
     */
    public JsonTask(String configPath, DriverLoader driverLoader) {
        this.driverLoader = driverLoader;
        createTaskFromFile(configPath);
    }

    /**
     * Загружает задание
     *
     * @param path - путь до конфигурации тестирования
     */
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
            System.err.println(e.getMessage());
        }
    }

    /**
     * Запускает тестирование
     */
    public void solveTask() {
        if (task == null)
            throw new NullPointerException("Test configuration not loaded");

        driver = driverLoader.getWebDriverFor((String) options.get("browser"));

        parseTask();
        driver.quit();
    }

    /**
     * По очередно выполняет каждое задание
     */
    private void parseTask() {
        for (Object o : task) {
            JSONObject json = (JSONObject) o;
            String action = json.getString("action");

            System.out.println("Do [action: " + action +
                    (json.has("desc") ? ", desc: " + json.getString("desc") + "]" : "]"));
            doAction(action, json);
        }
    }

    /**
     * Сохраняет параметры конфигурации в карту
     *
     * @param option - json объект с параметрами конфигурации
     * @return - карта параметров
     */
    private Map parseOption(JSONObject option) {
        return option.toMap();
    }

    /**
     * Метод находящий на текущей странице элемент (поддерживает все типы локаторов, поддерживаемые классом By
     *
     * @param findMethod - тип локатора (xpath e.g.)
     * @param target     - искомый элемент
     * @return - указатель на элемент
     * @see By
     */
    private By getDesiredElement(String findMethod, String target) {
        By desired = null;
        try {
            Method method = By.class.getMethod(findMethod, String.class);
            desired = (By) method.invoke(null, target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return desired;
    }

    /**
     * Поддержка тестирования клика по элементу страницы
     * @param json - параметры необходимые для прохождения теста
     */
    private void doClick(JSONObject json) {
        String findMethod = json.getString("type");
        String target = json.getString("target");

        driver.findElement(getDesiredElement(findMethod, target)).click();
    }

    /**
     * Поддержка вставки текста в тег
     * @param json - параметры необходимые для прохождения теста
     */
    private void doSetValue(JSONObject json) {
        String findMethod = json.getString("type");
        String target = json.getString("target");
        String value = json.getString("value");

        driver.findElement(getDesiredElement(findMethod, target)).sendKeys(value);
    }

    /**
     * Открытие указанной веб-страницы
     * @param json - параметры необходимые для прохождения теста
     */
    private void doOpenUrl(JSONObject json) {
        driver.get(json.getString("url"));
    }

    /**
     * Скриншот текущего содержимого
     * @param json - параметры необходимые для прохождения теста
     */
    private void doScreenshot(JSONObject json) {
        File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(tmp, new File(json.getString("fileName").concat(".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет отображен ли элемент на странице
     * @param json - параметры необходимые для прохождения теста
     */
    private void doCheckElementVisible(JSONObject json) {
        String findMethod = json.getString("type");
        String target = json.getString("target");

        By desired = getDesiredElement(findMethod, target);

        if (desired == null) {
            System.out.println("Cannot find element");
            return;
        }

        String res = driver.findElement(getDesiredElement(findMethod, target)).getCssValue("visibility");
        System.out.println("Target element is " + res);
    }

    /**
     * Запуск определенного теста
     * @param action - действие необходимое совершить (do.concat(action) = название требуемого метода)
     * @param obj - параметры для метода
     */
    private void doAction(String action, JSONObject obj) {
        try {
            Method invokedMethod = this.getClass().getDeclaredMethod("do".concat(firstUpperCase(action)), obj.getClass());
            invokedMethod.invoke(this, obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Дублирующий код
     * @see DefaultDriverLoader
     */
    private String firstUpperCase(String str) {
        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("Empty string");
        return str
                .substring(0, 1)
                .toUpperCase()
                .concat(str.substring(1));
    }

}
