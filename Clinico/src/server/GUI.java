package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {

    // GUI parameters
    private final int FONTSIZE = 18;

    private JsonArray allQuestions;
    private NewConnection connection;
    private String nurseLanguage;
    private JLabel loadingGif;
    private JTable questionsTable;

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
        // Change default font size
        Font font = (Font) UIManager.getLookAndFeelDefaults().get("Label.font");
        Font newFont = new Font(font.getName(), Font.PLAIN, 18);
        UIManager.put("Label.font", newFont);
        UIManager.put("CheckBox.font", newFont);
        UIManager.put("Button.font", newFont);
        //UIManager.put("JTable.font", newFont);

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
                System.out.println("File problem: Can't find file with questions");
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

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        int cursorX = 0;

        questionsPanel.add(new JLabel("Schmerzbild: Abdomineller Schmerz"));

        gbc.gridy++;

        String[] columnNames = {" ", "Fragen", "Antworten", "Dringlichkeit"};

        // This is a normal model that has checkboxes in the first column
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow first column to be edited (checkboxes)
                return column == 0;
            }
        };
        questionsTable = new JTable(model);


        JScrollPane jsp = new JScrollPane();
        model.setRowCount(nurseQuestions.size());
        model.setColumnCount(3);
        model.setColumnIdentifiers(columnNames);

        // Add question list with checkboxes
        for (String text : nurseQuestions) {

            model.setValueAt(false, cursorX, 0);

            model.setValueAt(text, cursorX, 1);

            cursorX++;

        }


        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(550);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        //questionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        questionsTable.setPreferredSize(new Dimension(1130, 300));
        jsp.setPreferredSize(new Dimension(1130, 350));

        // Add renderer for prio color
        questionsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer());

        // Change table fonts
        Font oldFont = questionsTable.getFont();
        questionsTable.setFont(new Font(oldFont.getName(), oldFont.getStyle(), FONTSIZE));
        Font oldHeaderFont = questionsTable.getTableHeader().getFont();
        questionsTable.getTableHeader().setFont(new Font(oldHeaderFont.getName(), oldHeaderFont.getStyle(), FONTSIZE));

        questionsTable.setRowHeight(FONTSIZE + 4);

        gbc.gridwidth = 4;

        jsp.setViewportView(questionsTable);
        questionsPanel.add(jsp, gbc);

        // Panel title
        questionsPanel.setBorder(BorderFactory.createTitledBorder("Wählen Sie Fragen aus, die Sie stellen möchten"));
        gbc.gridwidth = 1;
        gbc.gridy++;
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
                // Send allQuestions to client
                this.submitQuestions(allQuestions);
            }
        });

        finishButton.addActionListener(this::finishAction);

        root.add(questionsPanel);
    }

    private void submitQuestions(JsonArray allQuestions) {
        int col = 0;
        JsonArray a = new JsonArray();
        //
        for (int i = 0; i < this.questionsTable.getModel().getRowCount(); ++i) {
            boolean checked = (boolean) questionsTable.getValueAt(i, col);
            if (checked) {
                a.add(allQuestions.get(i));
                questionsTable.setValueAt(false, i, col);
                // Update color (gray)?
            }
        }
        if(a.size() >0) {
            loadingGif.setVisible(true);
            this.connection.sendToClient(a.toString());
        }
    }

    public void receiveAnswers(String answers) {
        String language = this.nurseLanguage;

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
                    this.questionsTable.setValueAt(answer, questionIndex, 2);
                    this.questionsTable.setValueAt(severity, questionIndex, 3);

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

    public void startGUI() {

        this.initGUI();
        initStartScreen();
        initQuestionPage();
        initReportScreen();
    }

    public void setConnection(NewConnection connection) {
        this.connection = connection;
    }

    private void finishAction(ActionEvent e) {
        // Generate the report
        StringBuilder report = new StringBuilder("Bericht:\nDer Patient hat:\n");
        final int col = 2;
        for (int i = 0; i < this.questionsTable.getModel().getRowCount(); ++i) {
            String answer = (String) this.questionsTable.getValueAt(i, col);
            if (answer != null && !answer.trim().equals("")) {
                report.append("- ").append(answer).append("\n");
            }
        }
        JPanel p = (JPanel) this.root.getComponent(2);
        JTextArea t = (JTextArea) p.getComponent(0);
        t.setText(report.toString());

        // Send the client the ending command
        this.connection.sendToClient("{\"command\": \"end\"}");

        // Go to next screen
        this.next();
    }

    /**
     * This class allows haveing a panel that has a background image
     */
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

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            Color[] colors = {Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED};

            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();


            //Get the status for the current row.
            if (col == 1) {
                l.setBackground(Color.GREEN);
            } else if (col == 3) {
                // Get prio
                Object val = tableModel.getValueAt(row, col);
                if (val != null && !val.equals("")) {
                    try {
                        int prio = (int) val; //Integer.parseInt((String) val);
                        l.setBackground(colors[prio]);
                    } catch (NumberFormatException e) {
                        // Don't change background color, this shouldn't happen
                        l.setBackground(Color.WHITE);
                    }
                } else {
                    l.setBackground(Color.WHITE);
                }
            } else {
                l.setBackground(Color.WHITE);
            }

            //Return the JLabel which renders the cell.
            return l;

        }
    }

}
