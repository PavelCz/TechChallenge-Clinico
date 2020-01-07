import java.util.Map;

public class Question {
    private String text;
    private Map<String, String> textTranslations;
    private int severity;
    private String[] answers;
    private Map<String, String>[] answerTranslations;

    public Question(String text, Map<String, String> textTranslations, int severity, String[] answers, Map<String, String>[] answerTranslations) {
        this.text = text;
        this.textTranslations = textTranslations;
        this.severity = severity;
        this.answers = answers;
        this.answerTranslations = answerTranslations;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getTextTranslations() {
        return textTranslations;
    }

    public void setTextTranslations(Map<String, String> textTranslations) {
        this.textTranslations = textTranslations;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public Map<String, String>[] getAnswerTranslations() {
        return answerTranslations;
    }

    public void setAnswerTranslations(Map<String, String>[] answerTranslations) {
        this.answerTranslations = answerTranslations;
    }
}
