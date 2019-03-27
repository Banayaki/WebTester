import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class JsonTask {

    private Map options;
    private JSONObject task;

    public JsonTask(String configPath, DriverLoader driverLoader) {

    }

    public void createTaskFromFile(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));


            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);


            JSONObject json = new JSONObject(stringBuilder.toString());
            task = json;

            if (!json.has("option") || !json.has("task"))
                throw new JSONException("Incorrect json format");

            options = parseOption(json.getJSONObject("option"));
            parseTask(json.getJSONArray("task"));

        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseTask(JSONArray task) {
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

    }

    private void doSetValue(JSONObject json) {

    }

    private void doOpenUrl(JSONObject json) {

    }

    private void doScreenshot(JSONObject json) {

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
