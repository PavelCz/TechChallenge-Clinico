import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<String> getAllQuestionsForLanguage(JsonArray questions, String language) {
        List<String> nurseQuestions = new ArrayList<>();
        questions.forEach(q ->
                nurseQuestions.add(
                        q.getAsJsonObject().get("translations").getAsJsonObject().get(language).getAsJsonObject()
                                .get("question").getAsString()
                )
        );
        return nurseQuestions;
    }

    private static void run() {
        String translationsPath = "data/translations.json";

        String nurseLanguage = "german";

        // Open the file and serialize the data into a json object
        FileReader fr = null;
        while (fr == null) {
            try {
                fr = new FileReader(translationsPath);
            } catch (FileNotFoundException e) {
                // Pass, retry the while loop
            }
        }
        JsonObject translation = JsonParser.parseReader(fr).getAsJsonObject();
        JsonArray questions = translation.get("abdominal").getAsJsonArray();


        List<String> nurseQuestions = getAllQuestionsForLanguage(questions, nurseLanguage);
        System.out.println(nurseQuestions);


    }

    public static void main(String[] args) {
        run();
    }
}
