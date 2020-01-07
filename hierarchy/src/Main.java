import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void run() {
        String translationsPath = "data/translations.json";

        String nurseLanguage = "german";

        FileReader fr = null;
        try {
            fr = new FileReader(translationsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = JsonParser.parseReader(fr).getAsJsonObject();

        System.out.println(jsonObject.get("abdominal").getAsJsonArray().get(0).getAsJsonObject().get("translations")
                .getAsJsonObject().get(nurseLanguage).getAsJsonObject());


    }

    public static void main(String[] args) {
        run();
    }
}
