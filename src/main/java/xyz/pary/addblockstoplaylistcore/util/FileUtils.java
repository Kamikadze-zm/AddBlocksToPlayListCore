package xyz.pary.addblockstoplaylistcore.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static java.util.stream.Collectors.toList;

public class FileUtils {

    /**
     * Возвращает строки из файла в кодировке cp1251, за исключением пустых строк и строк начинающихся с #
     *
     * @param path путь к файлу
     * @return список строк
     * @throws IOException в случае ошибок ввода/вывода
     */
    public static List<String> getLinesFromFile(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), Charset.forName("cp1251")).stream()
                .filter(s -> !(s.startsWith("#") || s.isEmpty()))
                .collect(toList());
    }

    /**
     * Добавляет разделитель пути к концу текущего пути (если необходимо)
     *
     * @param path путь к папке
     * @return исправленный путь
     */
    public static String fixFolderPath(String path) {
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }
}
