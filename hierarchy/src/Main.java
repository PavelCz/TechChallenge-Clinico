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
        questions.forEach(q -> {
                    String question = q.getAsJsonObject().get(language).getAsJsonObject().get("question").getAsString();
                    StringBuilder sb = new StringBuilder(question);
                    // Remove first seven Characters because they all say "Frage: "
                    sb.replace(0, 7, "");
                    nurseQuestions.add(sb.toString());
                }
        );
        return nurseQuestions;
    }

    private JPanel root;


    public void initGUI() {
        JFrame frame = new JFrame("Clinico Triage System");
        // Frame properties
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //root.setUndecorated(true);

        this.root = new JPanel();
        frame.add(root);
        frame.setVisible(true);

        this.root.setLayout(new CardLayout());
    }

    private void initQuestionPage() {

        // Open the Json file to read the translations
        String translationsPath = "data/translations.json";

        String nurseLanguage = "de";

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

        // Left side of the questionsPanel
        JPanel questionsPanel = new JPanel();

        // This layout aligns the checkboxes vertically
        questionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.ipadx = 5;
        // Number of colums
        //gbc.gridwidth = 2;
        // Number of lines (plus 1 for the button)
        //gbc.gridheight = nurseQuestions.size() + 1;

        //gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        questionsPanel.add(new JLabel("Questions"), gbc);
        gbc.gridx++;

        questionsPanel.add(new JLabel("Answers"), gbc);

        gbc.gridx++;
        questionsPanel.add(new JLabel("Severity"), gbc);

        gbc.gridx = 0;
        ++gbc.gridy;
        List<JCheckBox> checkboxes = new ArrayList<>();
        List<JLabel> answers = new ArrayList<>();
        List<JLabel> severities = new ArrayList<>();

        // Add question list with checkboxes
        for (String text : nurseQuestions) {
            JCheckBox checkbox = new JCheckBox(text, false);
            checkboxes.add(checkbox);

            questionsPanel.add(checkbox, gbc);
            gbc.gridx++;

            JLabel answerLabel = new JLabel("                    ");
            answers.add(answerLabel);
            questionsPanel.add(answerLabel, gbc);

            gbc.gridx++;

            JLabel sev = new JLabel("");
            severities.add(sev);
            questionsPanel.add(sev, gbc);

            gbc.gridx = 0;
            ++gbc.gridy;
        }

        // Panel title
        questionsPanel.setBorder(BorderFactory.createTitledBorder("Choose which questions you want to ask"));

        // Add submit button
        JButton submitButton = new JButton("Send questions");
        questionsPanel.add(submitButton, gbc);

        gbc.gridx++;

        // Gif from http://www.ajaxload.info/
        Icon icon2 = new ImageIcon("data/ajax-loader.gif");
        JLabel loadingGif = new JLabel(icon2);
        questionsPanel.add(loadingGif, gbc);

        loadingGif.setVisible(false);


        gbc.gridx++;

        JButton finishButton = new JButton("Finish");
        questionsPanel.add(finishButton, gbc);

        // Add button action
        submitButton.addActionListener(e -> {
            if (!loadingGif.isVisible()) {
                loadingGif.setVisible(true);
                this.submitQuestions(checkboxes, questions);
            } else {
                loadingGif.setVisible(false);
                this.handleAnswers(checkboxes, answers, severities);
            }
        });

        finishButton.addActionListener(e -> System.exit(0));

        root.add(questionsPanel);
    }

    private void submitQuestions(List<JCheckBox> checkboxes, JsonArray allQuestions) {
        JsonArray a = new JsonArray();
        for (int i = 0; i < checkboxes.size(); ++i) {
            JCheckBox cb = checkboxes.get(i);
            if (cb.isSelected()) {
                a.add(allQuestions.get(i));
            }
        }
        System.out.println(a);

    }

    private void handleAnswers(List<JCheckBox> checkboxes, List<JLabel> answers, List<JLabel> severities) {

        // Placeholder handling of answers and severities
        String[] possibleAnswers = {"Moderate pain", "Vomitting without blood", "1-2 times"};
        String[] answerSeverities = {"2", "1", "1"};
        Color[] colors = {Color.YELLOW, Color.GREEN, Color.GREEN};
        for (int i = 0; i < checkboxes.size(); ++i) {
            JCheckBox cb = checkboxes.get(i);
            if (cb.isSelected()) {
                cb.setSelected(false);
                cb.setForeground(Color.GRAY);
                answers.get(i).setText(possibleAnswers[i]);

                severities.get(i).setText(answerSeverities[i]);
                severities.get(i).setBackground(colors[i]);
                // This is necessary in order for the background to be painted
                severities.get(i).setOpaque(true);
            }
        }
    }

    private void next() {
        CardLayout c = (CardLayout) this.root.getLayout();
        c.next(this.root);
    }


    private void initStartScreen() {
        JPanel startPanel = new ImagePanel("data/mts.jpg");

        startPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton continueButton = new JButton("Translated triage");
        continueButton.addActionListener(e -> this.next());
        startPanel.add(continueButton);
        this.root.add(startPanel);
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.initGUI();
        m.initStartScreen();
        m.initQuestionPage();
    }

    private class ImagePanel extends JPanel {
        private String path;

        private ImagePanel(String path) {
            this.path = path;
        }

        @Override
        public void paintComponent(Graphics g) {
            Image image = new ImageIcon(path).getImage();
            g.drawImage(image, 0, 0, this);
        }
    }

}
