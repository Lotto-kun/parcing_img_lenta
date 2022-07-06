import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ParseImagesHtml {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseImagesHtml.class);
    private static final Marker EXCEPTIONS_MARKER = MarkerFactory.getMarker("EXCEPTIONS");
    private final String link;
    private Document doc = null;
    private final Set<String> allImages = new HashSet<>();

    public ParseImagesHtml(String link) {
        this.link = link;
    }

    public void parse() {
        System.out.println("Начинаем парсинг");
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            LOGGER.error(EXCEPTIONS_MARKER, "Не смогло подключиться по ссылке", e);
            e.printStackTrace();
        }
        if (doc != null) {
            Elements imageTags = doc.getElementsByTag("img");
            imageTags.forEach(image -> {
                String link = image.attr("abs:src");
                allImages.add(link);
            });
            System.out.println("Парсинг завершен");
        } else {
            System.out.println("Ошибка получения кода. Проверьте правильность ссылки");
        }
    }

    public void download(String path) {
        Path destination = Paths.get(path);
        if (Files.notExists(destination)) {
            try {
                Files.createDirectories(destination);
            } catch (Exception e) {
                LOGGER.error(EXCEPTIONS_MARKER, "ошибка создания директории назначения", e);
                e.printStackTrace();
                return;
            }
        }

        System.out.println("Начинаем скачивание файлов в папку");
        allImages.forEach(image -> {
            try {
                String imageName = getFileName(image);
                InputStream in = new URL(image).openStream();
                Files.copy(in, destination.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
                in.close();
            } catch (IOException e) {
                LOGGER.error(EXCEPTIONS_MARKER, "ошибка при скачивании картинки", e);
                e.printStackTrace();
            }
        });
        System.out.println("Загружено в папку " + Objects.requireNonNull(destination.toFile().listFiles()).length + " шт");
    }

    private String getFileName(String link) {
        String[] tokens = link.split("/");
        if (tokens[tokens.length - 1].contains("?")) {
            tokens[tokens.length - 1] = tokens[tokens.length - 1].substring(0, tokens[tokens.length - 1].indexOf("?"));
        }
        return tokens[tokens.length - 1];
    }

    public void printParsedLinks() {
        for (String imageLinkText : allImages) {
            System.out.println(imageLinkText);
        }
        System.out.println("Всего получено ссылок на картинки: " + allImages.size() + " шт.");
    }

}
