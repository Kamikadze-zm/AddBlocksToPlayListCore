package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.kamikadze_zm.addblockstoplaylistcore.util.FileUtils;

public class Exclusions {

    private final List<String> exclusions;

    /**
     * Пустой список исключений
     */
    public Exclusions() {
        this.exclusions = new ArrayList<>();
    }

    public Exclusions(String file) throws IOException {
        this.exclusions = FileUtils.getLinesFromFile(file);
    }

    public void add(String exclusion) {
        this.exclusions.add(exclusion);
    }

    /**
     * Проверка является ли файл исключением методом startsWith. Между строками действует правило ИЛИ, между путями в строке после разделителей И НЕ.
     * Разделитель - |
     *
     * @param verifiablePath путь проверяемого файла
     * @return {@code true} если указанный путь является исключением
     */
    public boolean isExclusionByStartsWith(String verifiablePath) {
        if (!exclusions.isEmpty()) {
            for (String exc : exclusions) {
                exc = exc.toLowerCase();
                verifiablePath = verifiablePath.toLowerCase();
                String[] parts = exc.split("\\|");
                boolean isExclusion = verifiablePath.startsWith(parts[0].trim());
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        isExclusion = isExclusion && !verifiablePath.startsWith(parts[i].trim());
                    }
                }
                if (isExclusion) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверка является ли файл исключением методом contains. Между строками действует правило ИЛИ, между путями в строке после разделителей И НЕ.
     * Разделитель - |
     *
     * @param verifiablePath путь проверяемого файла
     * @return {@code true} если указанный путь является исключением
     */
    public boolean isExclusionByContains(String verifiablePath) {
        if (!exclusions.isEmpty()) {
            for (String exc : exclusions) {
                exc = exc.toLowerCase();
                verifiablePath = verifiablePath.toLowerCase();
                String[] parts = exc.split("\\|");
                boolean isExclusion = verifiablePath.contains(parts[0].trim());
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        isExclusion = isExclusion && !verifiablePath.contains(parts[i].trim());
                    }
                }
                if (isExclusion) {
                    return true;
                }
            }
        }
        return false;
    }
}
