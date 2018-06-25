package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.BlockTime;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.addblockstoplaylistcore.util.FileUtils;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.parameter.Duration;
import ru.kamikadze_zm.onair.command.parameter.Fade;
import ru.kamikadze_zm.onair.command.parameter.MarkIn;
import ru.kamikadze_zm.onair.command.parameter.ParallelDuration;

public class PopUpAd {

    private static final Logger LOG = LogManager.getLogger(PopUpAd.class);

    private int commerceCrawlLineDuration;
    private List<Command> popUpAds;
    private int currentPopUpAd = 0;
    private List<String> popUpAdErrors;

    private boolean isCreated = false;

    private boolean onCrawlLine = false;

    public PopUpAd(Settings settings, int commerceCrawlLineDuration) {
        if (settings.onPopUpAd) {
            this.popUpAdErrors = new ArrayList<>();
            //длительность коммерческой строки + отступ, в милисекундах
            this.commerceCrawlLineDuration = (commerceCrawlLineDuration
                    + Integer.parseInt(settings.getParameter(SettingsKeys.POP_UP_AD_INTERVAL))) * 1000;
            try {
                List<String> popUpAdNames = FileUtils.getLinesFromFile(settings.getParameter(SettingsKeys.POP_UP_AD_FILE));
                this.popUpAds = new ArrayList<>();
                popUpAdNames.stream().forEach(name -> this.popUpAds.add(
                        new TitleObjLoad(settings.getParameter(SettingsKeys.POP_UP_AD_OBJECT_NAME),
                                new ParallelDuration(0, 0, 15, 0),
                                null,
                                FileUtils.fixFolderPath(settings.getParameter(SettingsKeys.POP_UP_AD_PATH)) + name)));
            } catch (IOException e) {
                LOG.warn("Cannot read pop up ad file: ", e);
                this.popUpAdErrors.add("Не удалось прочитать файл с коммерческими плашками");
            }
            if (!this.popUpAds.isEmpty()) {
                isCreated = true;
                if (Boolean.parseBoolean(settings.getParameter(SettingsKeys.POP_UP_AD_SHUFFLE))) {
                    Collections.shuffle(this.popUpAds);
                }
                if (settings.onCrawlLine) {
                    this.onCrawlLine = true;
                }
            } else {
                this.popUpAdErrors.add("Не найдены названия коммерческих плашек");
            }
        }
    }

    /**
     * Формирует список команд для коммерческой плашки. Команды:
     * <pre>
     * Часть видео под коммерческую бегущую строку, идущую после рекламного блока
     * Коммерческая плашка
     * Команда окончания рекламного блока (если не равна {@code null})
     * Метка сброса счетчика для прочей строки (если включена)
     * Часть видео на которой выйдет коммерческая плашка
     * </pre>
     *
     * @param movie видео, на которое нужно поставить коммерческую плашку
     * @param endBlockCommand команда окончания рекламного блока
     * @param blockTime время блока (для добавления в ошибку)
     * @return список команд для коммерческой плашки, если плашек нет или длительность видео меньше длительности коммерческой строки – {@code null}
     * (будет добавлена ошибка)
     */
    public List<Command> formPopUpAdCommands(Movie movie, Command endBlockCommand, BlockTime blockTime) {
        if (!isCreated) {
            return null;
        }

        Duration sourceDuration = movie.getDuration();

        if (sourceDuration.getDuration() < commerceCrawlLineDuration) {
            popUpAdErrors.add("Блок: " + blockTime + ". Длительность файла меньше длительности коммерческой строки");
            return null;
        }

        MarkIn sourceMarkIn = movie.getMarkIn();
        if (sourceMarkIn == null) {
            sourceMarkIn = new MarkIn(0, 0, 0, 0);
        }

        Fade sourceFade = movie.getFade();

        List<Command> popUpAdCommands = new ArrayList<>(5);

        //часть на которой выйдет коммерческая строка
        Movie commerceCrawlLinePart = new Movie(
                sourceMarkIn,
                new Duration(commerceCrawlLineDuration),
                sourceFade,
                movie.getFileName());
        popUpAdCommands.add(commerceCrawlLinePart);

        //вставка коммерческой плашки
        Command popUpAd = getCurrentPopUpAd();
        popUpAdCommands.add(popUpAd);

        //вставка команды окончания рекламного блока
        if (endBlockCommand != null) {
            popUpAdCommands.add(endBlockCommand);
        }

        //метка для прочей строки чтоб сбросила счетчик
        if (onCrawlLine) {
            popUpAdCommands.add(new Movie(null, null, null, Settings.NO_OTHER_CRAWL_LINE_MARK));
        }

        //часть на которой выйдет коммерческая плашка
        Movie popUpAdPart = new Movie(
                new MarkIn(sourceMarkIn.getDuration() + commerceCrawlLineDuration),
                new Duration(sourceDuration.getDuration() - commerceCrawlLineDuration),
                sourceFade,
                movie.getFileName());
        popUpAdCommands.add(popUpAdPart);
        return popUpAdCommands;
    }

    /**
     *
     * @return список ошибок, возникших при создании и добавлении коммерческих плашек, или пустой список если ошибки отсутствуют
     */
    public List<String> getPopUpAdErrors() {
        if (popUpAdErrors != null) {
            return popUpAdErrors;
        } else {
            return Collections.emptyList();
        }
    }

    private Command getCurrentPopUpAd() {
        Command popUpAd = popUpAds.get(currentPopUpAd);
        currentPopUpAd++;
        if (currentPopUpAd == popUpAds.size()) {
            currentPopUpAd = 0;
        }
        return popUpAd;
    }
}
