package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonArray allQuestions;
    private NewConnection connection;
    private String nurseLanguage;
    private List<JCheckBox> checkboxes;
    private List<JLabel> answers;
    private List<JLabel> severities;
    private JLabel loadingGif;

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
    private List<String> collectedAnswers = new ArrayList<>();
    private HashMap<String, Integer> indexMap;


    private int tagToIndex(String tag) {
        if (this.indexMap == null) {
            this.indexMap = new HashMap<>();
            for (int i = 0; i < this.allQuestions.size(); ++i) {
                indexMap.put(this.allQuestions.get(i).getAsJsonObject().get("tag").getAsString(), i);
            }
        }
        return indexMap.get(tag);
    }


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
        nurseLanguage = "de";

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
        allQuestions = translation.get("abdominal").getAsJsonArray();


        List<String> nurseQuestions = getAllQuestionsForLanguage(allQuestions, nurseLanguage);
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
        questionsPanel.add(new JLabel("Fragen"), gbc);
        gbc.gridx++;

        questionsPanel.add(new JLabel("Antworten"), gbc);

        gbc.gridx++;
        questionsPanel.add(new JLabel("Severity"), gbc);

        gbc.gridx = 0;
        ++gbc.gridy;
        checkboxes = new ArrayList<>();
        answers = new ArrayList<>();
        severities = new ArrayList<>();

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
        questionsPanel.setBorder(BorderFactory.createTitledBorder("Wählen Sie Fragen aus, die Sie stellen möchten"));

        // Add submit button
        JButton submitButton = new JButton("Fragen absenden");
        questionsPanel.add(submitButton, gbc);

        gbc.gridx++;

        // Gif from http://www.ajaxload.info/
        Icon icon2 = new ImageIcon("data/ajax-loader.gif");
        loadingGif = new JLabel(icon2);
        questionsPanel.add(loadingGif, gbc);

        loadingGif.setVisible(false);


        gbc.gridx++;

        JButton finishButton = new JButton("Beenden");
        questionsPanel.add(finishButton, gbc);

        // Add button action
        submitButton.addActionListener(e -> {
            if (!loadingGif.isVisible()) {
                loadingGif.setVisible(true);
                // Send allQuestions to client
                this.submitQuestions(checkboxes, allQuestions);

                // Receive answers
                String answer = "";
                /*try {
                    answer = this.in.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                //System.out.println("Answer:");
                //System.out.println(answer);
                //loadingGif.setVisible(false);

                //JsonArray answersArray = JsonParser.parseString(answer).getAsJsonArray();


                // Visualize answers
                //this.handleAnswers(checkboxes, answers, severities);
            }
            /*else {
                loadingGif.setVisible(false);
                this.handleAnswers(checkboxes, answers, severities);

                // I get some kind of answer list.JSON Object?
                collectedAnswers.add("Moderate Schmerzen");
                collectedAnswers.add("Normale Atmung");
                collectedAnswers.add("Kein Übergeben");
                //collectedAnswers.add("Normaler Stuhlgang");
                collectedAnswers.add("Leichtes Fieber");
                String report = "Bericht:\nDer Patient hatte:\n";
                for (String answer : collectedAnswers) {
                    report += "- ";
                    report += answer;
                    report += "\n";
                }


                JPanel p = (JPanel) this.root.getComponent(2);
                JTextArea t = (JTextArea) p.getComponent(0);
                t.setText(report);

            }*/
        });

        finishButton.addActionListener(e -> this.next());

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
        System.out.println("Sending Questions:");
        System.out.println(a);
        //this.out.println(a);
        this.connection.setOutgoingMessage(allQuestions.toString());
        this.connection.sendToClient(allQuestions.toString());


        for (int i = 0; i < checkboxes.size(); ++i) {
            JCheckBox cb = checkboxes.get(i);
            if (cb.isSelected()) {
                cb.setSelected(false);
                cb.setForeground(Color.GRAY);
            }
        }

    }

    public void receiveAnswers(String answers) {
        String language = this.nurseLanguage;
        Color[] colors = {Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED};

        JsonObject jsonObj = JsonParser.parseString(answers).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            String answerTag = entry.getKey();
            int answerIndex = entry.getValue().getAsInt();

            // Find the information about answer in nurse language
            for (JsonElement questionObject : this.allQuestions) {
                String questionTag = questionObject.getAsJsonObject().get("tag").getAsString();

                // Find the question this tag belongs to
                if (answerTag.equals(questionTag)) {
                    int severity = questionObject.getAsJsonObject().get("severity").getAsJsonArray().get(answerIndex).getAsInt();
                    String answer = questionObject.getAsJsonObject().get(language).getAsJsonObject().get("choices").getAsJsonArray().get(answerIndex).getAsString();
                    int questionIndex = tagToIndex(questionTag);
                    // Update this answer in the GUI

                    this.answers.get(questionIndex).setText(answer);
                    this.severities.get(questionIndex).setText("" + severity);
                    this.severities.get(questionIndex).setBackground(colors[answerIndex]);
                    // Necessary for background to be painted
                    severities.get(questionIndex).setOpaque(true);

                    // Go to next answer from client
                    break;
                }
            }
        }
        loadingGif.setVisible(false);
    }

    private void next() {
        CardLayout c = (CardLayout) this.root.getLayout();
        c.next(this.root);
    }


    private void initStartScreen() {
        JPanel startPanel = new ImagePanel("data/mts.jpg");

        startPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton continueButton = new JButton("Triage Übersetzung");
        continueButton.addActionListener(e -> this.next());
        startPanel.add(continueButton);
        this.root.add(startPanel);
    }

    private void initReportScreen() {
        JPanel panel = new JPanel();
        JTextArea t = new JTextArea("Bericht");
        panel.add(t);

        JButton continueButton = new JButton("Fertigstellen");
        continueButton.addActionListener(e -> this.next());
        panel.add(continueButton);
        this.root.add(panel);
    }

    public static void main(String[] args) {
        GUI m = new GUI();
        m.initGUI();
        m.initStartScreen();
        m.initQuestionPage();
        m.initReportScreen();
    }

    public void startGUI() {

        this.initGUI();
        initStartScreen();
        initQuestionPage();
        initReportScreen();
    }

    public void addSocket(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void setConnection(NewConnection connection) {
        this.connection = connection;
    }

    private class ImagePanel extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
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
