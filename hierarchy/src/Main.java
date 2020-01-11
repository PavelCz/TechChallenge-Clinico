import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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


        int xCursor = 0;
        int yCursor = 0;

        gbc.gridx = xCursor;
        gbc.gridy = yCursor;
        panel.add(new JLabel("Questions"), gbc);
        xCursor++;

        gbc.gridx = xCursor;
        gbc.gridy = yCursor;
        panel.add(new JLabel("Answers"), gbc);

        xCursor = 0;
        ++yCursor;

        for (String text : nurseQuestions) {
            gbc.gridx = xCursor;
            gbc.gridy = yCursor;
            JCheckBox checkbox = new JCheckBox(text, false);
            panel.add(checkbox, gbc);
            xCursor++;

            gbc.gridx = xCursor;
            gbc.gridy = yCursor;
            JLabel answerPanel = new JLabel("           ");
            panel.add(answerPanel, gbc);

            xCursor = 0;
            ++yCursor;
        }

        // Panel title
        panel.setBorder(BorderFactory.createTitledBorder("Choose which questions you want to ask"));

        gbc.gridx = xCursor;
        gbc.gridy = yCursor;

        // Add submit button
        JButton submitButton = new JButton("Send questions");
        panel.add(submitButton, gbc);

        xCursor++;

        gbc.gridx = xCursor;
        gbc.gridy = yCursor;

        // Gif from http://www.ajaxload.info/

        Icon icon2 = new ImageIcon("data/ajax-loader.gif");
        JLabel loadingGif = new JLabel(icon2);
        panel.add(loadingGif, gbc);



        frame.add(panel);

        //frame.pack();
        frame.setSize(1024, 512);
        frame.setVisible(true);


    }

    public static void main(String[] args) {
        run();
    }
}
