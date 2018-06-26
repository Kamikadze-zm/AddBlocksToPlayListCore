package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.io.IOException;
import java.util.ArrayList;
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

public class TobaccoInserter extends AdBlockDependentInserter {

    private static final Logger LOG = LogManager.getLogger(TobaccoInserter.class);

    private Command tobacco;
    private Exclusions tobaccoExclusions;

    public TobaccoInserter(Settings settings, Parameters parameters) {
        super(settings, parameters);

        if (!settings.onTobacco) {
            String message = "TobaccoInserter cannot be used, when setting onTobacco is not true";
            LOG.error(message);
            throw new IllegalStateException(message);
        }

        //команда табака (до версии форварда 5.8 был TitleMovie)
        this.tobacco = new TitleObjLoad(settings.getParameter(SettingsKeys.TOBACCO_OBJECT_NAME),
                new ParallelDuration(0, 0, 5, 0),
                null,
                settings.getParameter(SettingsKeys.TOBACCO_PATH));

        try {
            this.tobaccoExclusions = new Exclusions(settings.getParameter(SettingsKeys.TOBACCO_EXCLUSIONS));
        } catch (IOException e) {
            LOG.warn("Cannot read tobacco exclusions file: ", e);
            this.errors.add("Не удалось прочитать файл исключений табака. Табак расставлен на всё.");
        }
    }

    @Override
    public List<Command> process(List<Command> schedule) {
        List<Command> outSchedule = new ArrayList<>();
        String lastMovieFileName = "";

        for (Command c : schedule) {

            if (CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;

                //пропуск меток для строки
                if (settings.onCrawlLine && m.getFileName().equals(Settings.NO_OTHER_CRAWL_LINE_MARK)) {
                    outSchedule.add(c);
                    continue;
                }

                //начало видео
                if (!lastMovieFileName.equals(m.getFileName()) || m.getMarkIn() == null) {
                    String fn = m.getFileName();

                    //не входит в исключения
                    if (tobaccoExclusions == null || !tobaccoExclusions.isExclusionByStartsWith(fn)) {
                        lastMovieFileName = fn;
                        checkEndAdBlockAndAddCommand(outSchedule, tobacco);
                    }
                }
            }
            outSchedule.add(c);
        }
        return outSchedule;
    }

    public void setTobaccoCommand(Command tobacco) {
        this.tobacco = tobacco;
    }
}
