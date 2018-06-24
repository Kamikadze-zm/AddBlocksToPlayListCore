package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.util.ArrayList;
import java.util.List;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.parameter.Duration;
import ru.kamikadze_zm.addblockstoplaylistcore.util.FileUtils;

public class Trailers {

    private final List<Movie> trailers;
    private int currentTrailer = 0;

    /**
     * Создает указанное кол-во анонсов-трейлеров. Название каждого состоит из: trailersPath + prefix + номер + .avi
     *
     * @param trailersPath путь к папке с анонсами-трейлерами
     * @param prefix префикс названия
     * @param trailersNumber кол-во анонсов-трейлеров
     */
    public Trailers(String trailersPath, String prefix, int trailersNumber) {
        trailers = new ArrayList<>();
        String fixedPath = FileUtils.fixFolderPath(trailersPath);
        for (int i = 1; i <= trailersNumber; i++) {
            trailers.add(new Movie(null, new Duration(0, 1, 0, 0), null, fixedPath + prefix + i + ".avi"));
        }
    }

    /**
     *
     * @return текущий анонс-трейлер
     */
    public Movie getCurrentTrailer() {
        Movie t = trailers.get(currentTrailer);
        currentTrailer++;
        if (currentTrailer == trailers.size()) {
            currentTrailer = 0;
        }
        return t;
    }
}
