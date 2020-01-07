import com.google.cloud.translate.*;


public class Main {

    public static void main(String[] args) {

        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate("¡Hola Mundo!");
        System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());
    }
}
