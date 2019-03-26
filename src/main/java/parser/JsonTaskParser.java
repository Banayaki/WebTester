package parser;

import org.json.JSONObject;

import java.io.*;

public class JsonTaskParser {

    private Task task;

    public void createTaskFromFile(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));


            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);


            JSONObject json = new JSONObject(stringBuilder.toString());

            json.query()
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
