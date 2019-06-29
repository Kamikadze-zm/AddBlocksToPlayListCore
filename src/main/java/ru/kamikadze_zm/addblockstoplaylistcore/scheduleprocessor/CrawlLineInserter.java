package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.Parameters;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Command.CommandKey;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.parameter.ParallelDuration;

public class CrawlLineInserter extends AdBlockDependentInserter {

    private static final Logger LOG = LogManager.getLogger(CrawlLineInserter.class);

    private final TitleObjLoad otherCrawlLine;

    private Exclusions crawlLineExclusions;
    private Exclusions fullExclusions;

    public CrawlLineInserter(Settings settings, Parameters parameters, Date scheduleDate) {
        super(settings, parameters);

        if (scheduleDate == null) {
            String message = "ScheduleDate cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        if (!settings.getBoolParameter(SettingsKeys.ON_CRAWL_LINE)) {
            String message = "CrawlLineInserter cannot be used, when setting " + SettingsKeys.ON_CRAWL_LINE.getKey() + " is not true";
            LOG.error(message);
            throw new IllegalStateException(message);
        }

        String clObjectName = settings.getParameter(SettingsKeys.CRAWL_LINE_NAME);
        ParallelDuration clDuration = new ParallelDuration(0, 0, 0, 1);

        //форматирование пути к файлу заданию для строки (D:\\Строка\\<> СЭТ прочие.txt в <> подставляется отформатированная дата)
        //формат даты для строки
        SimpleDateFormat df = new SimpleDateFormat(settings.getParameter(SettingsKeys.CRAWL_LINE_DATE_FORMAT));

        this.otherCrawlLine = new TitleObjLoad(clObjectName, clDuration, null,
                settings.getParameter(SettingsKeys.CRAWL_LINE_OTHER_PATH).replace("<>", df.format(scheduleDate)));

        //искоючения для строки
        try {
            crawlLineExclusions = new Exclusions(settings.getParameter(SettingsKeys.CRAWL_LINE_EXCLUSIONS));
        } catch (IOException e) {
            LOG.warn("Cannot read crawlline exclusions file: ", e);
            this.errors.add("Не удалось прочитать файл исключений для прочей строки. Она расставлена на всё.");
        }

        this.fullExclusions = SharedObjects.getFullExclusions(settings);
        if (this.fullExclusions == null) {
            this.errors.add("Не удалось прочитать файл исключений для детских и местных программ. "
                    + "Если они разрезаны на части на них расставлена прочая строка.");
        }
    }

    @Override
    public List<Command> process(List<Command> schedule) {
        List<Command> outSchedule = new ArrayList<>();
        String lastMovieFileName = "";
        int skipped = 0; //счетчик пропущенных частей
        for (Command c : schedule) {
            if (CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;

                if (m.getFileName().equals(Settings.NO_OTHER_CRAWL_LINE_MARK)) {
                    skipped = 0;
                    continue;
                }

                if ((fullExclusions != null && fullExclusions.isExclusionByContains(m.getFileName()))
                        || (crawlLineExclusions != null && crawlLineExclusions.isExclusionByStartsWith(m.getFileName()))) {
                    skipped = 0;
                    outSchedule.add(c);
                    continue;
                }

                //начало видео
                if (!lastMovieFileName.equals(m.getFileName())) {
                    lastMovieFileName = m.getFileName();
                    //ставим после этого
                    skipped = 1;
                } else if (skipped == 1) {// ставим через одну часть
                    checkEndAdBlockAndAddCommand(outSchedule, otherCrawlLine);
                    skipped = 0;
                } else {
                    //пропускаем часть
                    skipped++;
                }
            }
            outSchedule.add(c);
        }
        return outSchedule;
    }
}
