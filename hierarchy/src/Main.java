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

    private JFrame frame;
    private int state;

    public Main() {
        this.frame = new JFrame("Clinico Triage System");
        this.state = 0;
    }

    public void initGUI() {
        // Frame properties
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);
    }

    private void startQuestionChooser() {

        this.frame.removeAll();

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

        // Left side of the panel
        JPanel panel = new JPanel();

        // This layout aligns the checkboxes vertically
        panel.setLayout(new GridBagLayout());
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
        panel.add(new JLabel("Questions"), gbc);
        gbc.gridx++;

        panel.add(new JLabel("Answers"), gbc);

        gbc.gridx++;
        panel.add(new JLabel("Severity"), gbc);

        gbc.gridx = 0;
        ++gbc.gridy;
        List<JCheckBox> checkboxes = new ArrayList<>();
        List<JLabel> answers = new ArrayList<>();
        List<JLabel> severities = new ArrayList<>();

        // Add question list with checkboxes
        for (String text : nurseQuestions) {
            JCheckBox checkbox = new JCheckBox(text, false);
            checkboxes.add(checkbox);

            panel.add(checkbox, gbc);
            gbc.gridx++;

            JLabel answerLabel = new JLabel("                    ");
            answers.add(answerLabel);
            panel.add(answerLabel, gbc);

            gbc.gridx++;

            JLabel sev = new JLabel("");
            severities.add(sev);
            panel.add(sev, gbc);

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


        gbc.gridx++;

        JButton finishButton = new JButton("Finish");
        panel.add(finishButton, gbc);

        // Placeholder handling of answers and severities
        String[] possibleAnswers = {"Moderate pain", "Vomitting without blood", "1-2 times"};
        String[] answerSeverities = {"2", "1", "1"};
        Color[] colors = {Color.YELLOW, Color.GREEN, Color.GREEN};
        // Add button action
        submitButton.addActionListener(e -> {
            if (!loadingGif.isVisible()) {
                loadingGif.setVisible(true);

            } else {
                loadingGif.setVisible(false);
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
        });

        finishButton.addActionListener(e -> System.exit(0));

        frame.add(panel);
    }


    private void initStartScreen() {
        ImagePanel panel = new ImagePanel("data/mts.jpg");
        //panel.setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton continueButton = new JButton("Translated triage");
        continueButton.addActionListener(e -> state = 1);
        panel.add(continueButton );
        this.frame.add(panel);
        //frame.setSize(1024, 512);
        this.frame.setVisible(true);
    }

    private void run() {
        while (this.state == 0) {
            if(this.state == 1) {
                break;
            }
        }
        System.out.println("Change screen");
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.initGUI();
        m.initStartScreen();
        // Blocks until state is changed
        m.run();
        m.startQuestionChooser();
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
