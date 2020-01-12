import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
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
        // Open the Json file to read the translations
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

        // Create backend GUI

        // Frame properties
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Clinico Triage System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Left side of the panel
        JPanel panel = new JPanel();

        // This layout aligns the checkboxes vertically
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        // Number of colums
        //gbc.gridwidth = 2;
        // Number of lines (plus 1 for the button)
        //gbc.gridheight = nurseQuestions.size() + 1;

        //gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Questions"), gbc);
        gbc.gridx++;

        panel.add(new JLabel("Answers"), gbc);

        gbc.gridx = 0;
        ++gbc.gridy;
        List<JCheckBox> checkboxes = new ArrayList<>();
        List<JLabel> answers = new ArrayList<>();

        // Add question list with checkboxes
        for (String text : nurseQuestions) {
            JCheckBox checkbox = new JCheckBox(text, false);
            checkboxes.add(checkbox);

            panel.add(checkbox, gbc);
            gbc.gridx++;

            JLabel answerLabel = new JLabel("                    ");
            answers.add(answerLabel);
            panel.add(answerLabel, gbc);

            gbc.gridx = 0;
            ++gbc.gridy;
        }

        // Panel title
        panel.setBorder(BorderFactory.createTitledBorder("Choose which questions you want to ask"));

        // Add submit button
        JButton submitButton = new JButton("Send questions");
        panel.add(submitButton, gbc);

        gbc.gridx++;

        // Gif from http://www.ajaxload.info/
        Icon icon2 = new ImageIcon("data/ajax-loader.gif");
        JLabel loadingGif = new JLabel(icon2);
        panel.add(loadingGif, gbc);

        loadingGif.setVisible(false);

        String[] possibleAnswers = {"Moderate pain", "Vomitting without blood", "1-2 times"};

        // Add button action
        submitButton.addActionListener(e -> {
            if (!loadingGif.isVisible()) {
                loadingGif.setVisible(true);

            } else {
                loadingGif.setVisible(false);
                for (int i = 0; i < checkboxes.size(); ++i) {
                    if (checkboxes.get(i).isSelected()) {
                        checkboxes.get(i).setSelected(false);
                        answers.get(i).setText(possibleAnswers[i]);
                    }
                }
            }
        });

        frame.add(panel);

        //frame.pack();
        frame.setSize(1024, 512);
        frame.setVisible(true);


    }

    public static void main(String[] args) {
        run();
    }
}
