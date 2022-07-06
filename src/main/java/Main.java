public class Main {
    public static final String LINK_TO_PARSE = "https://lenta.ru/";
    public static final String FOLDER_TO_SAVE = "lenta";
    public static void main(String[] args) {
        ParseImagesHtml lentaRu = new ParseImagesHtml(LINK_TO_PARSE);
        lentaRu.parse();
        lentaRu.printParsedLinks();
        lentaRu.download(FOLDER_TO_SAVE);
    }
}
