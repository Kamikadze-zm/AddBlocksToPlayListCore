package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.Parameters;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.AdBlock;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.AdBlocks;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.BlockTime;
import ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.AbstractInserter;
import ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.Exclusions;
import ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.SharedObjects;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Command.CommandKey;
import ru.kamikadze_zm.onair.command.Comment;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjLoad;

public class AdBlocksInserter extends AbstractInserter {

    private static final Logger LOG = LogManager.getLogger(AdBlocksInserter.class);

    private final AdBlocks adBlocks;

    private Trailers firstTrailers;
    private Trailers secondTrailers;

    private final AdBlockDecor adBlockDecor;

    private final EndBlockCommand endBlockCommand;

    private final AdBlockCrawlLine adBlockCrawlLine;

    private Exclusions fullExclusions;

    private PopUpAd popUpAd;

    public AdBlocksInserter(Settings settings, AdBlocks adBlocks, Date scheduleDate, Parameters params) {
        super(settings);
        if (adBlocks == null || scheduleDate == null || params == null) {
            LOG.error("Constructor parameters cannot be null: adblocks = {}, scheduleDate = {}, params = {}", adBlocks, scheduleDate, params);
            throw new IllegalArgumentException("Constructor parameters cannot be null");
        }

        if (!settings.getBoolParameter(SettingsKeys.ON_AD_BLOCKS)) {
            String message = "AdBlocksInserter cannot be used, when setting " + SettingsKeys.ON_AD_BLOCKS.getKey() + " is not true";
            LOG.error(message);
            throw new IllegalStateException(message);
        }

        this.adBlocks = adBlocks;

        String trailersPath = settings.getParameter(SettingsKeys.TRAILERS_PATH);
        this.firstTrailers = new Trailers(trailersPath, "anons1_", params.getTrailersNumber());
        if (settings.getBoolParameter(SettingsKeys.ON_SECOND_TRAILER)) {
            this.secondTrailers = new Trailers(trailersPath, "anons2_", params.getSecondTrailersNumber());
        }

        this.adBlockDecor = SharedObjects.getAdBlockDecor(settings);

        this.endBlockCommand = SharedObjects.getEndBlockCommand(settings, params);

        this.adBlockCrawlLine = new AdBlockCrawlLine(settings, scheduleDate);

        //исключения для строк, плашек (на детские программы, программы местные и т.д.)
        this.fullExclusions = SharedObjects.getFullExclusions(settings);
        if (this.fullExclusions == null) {
            this.errors.add("Не удалось прочитать файл исключений для детских и местных программ. "
                    + "На них расставлены строки и плашки после рекламных блоков.");
        }

        if (settings.getBoolParameter(SettingsKeys.ON_POP_UP_AD)) {
            this.popUpAd = new PopUpAd(settings, params.getCommerceCrawlLineDuration());
        }
    }

    @Override
    public List<Command> process(List<Command> schedule) {
        List<Command> outSchedule = new ArrayList<>();

        boolean needPopUpAd = false;
        BlockTime lastBlockTime = null;

        int index = -1;
        for (Command command : schedule) {//? iterator

            index++; //?

            //убираем паузы и команды ожидания
            if (CommandKey.PAUSE == command.getCommandKey() || CommandKey.WAIT_OPERATOR == command.getCommandKey()
                    || CommandKey.WAIT_FOLLOW == command.getCommandKey()) {
                continue;
            }

            //рекламный блок
            if (CommandKey.COMMENT == command.getCommandKey()) {
                String c = ((Comment) command).getComment();

                AdBlock block = adBlocks.getBlock(c);
                if (block == null) {
                    outSchedule.add(command);
                    continue;
                }
                addAdBlock(outSchedule, block);

                boolean exclusion = checkFullExclusion(schedule, index);

                if (!exclusion) {
                    TitleObjLoad crawlLine = getAdBlockCrawlLine(block.getTime());
                    if (crawlLine != null) {
                        outSchedule.add(crawlLine);
                    }
                }

                //проверка на коммерческую плашку после 15 минутных блоков
                if (settings.getBoolParameter(SettingsKeys.ON_POP_UP_AD) && !exclusion && block.getTime().getMinutes() == 15) {
                    needPopUpAd = true;
                    lastBlockTime = block.getTime();
                } else {
                    outSchedule.add(getEndBlockCommand(block.getTime(), exclusion));
                }
                //если нужна коммерческая плашка, ставится на первом видео после рекламы
            } else if (needPopUpAd) {
                if (CommandKey.MOVIE == command.getCommandKey()) {
                    addPopUpAd(outSchedule, (Movie) command, lastBlockTime);

                    needPopUpAd = false;
                    lastBlockTime = null;
                }
            } else {
                outSchedule.add(command);
            }
        }

        addErrors();
        addAdditionalInfo();
        return outSchedule;
    }

    /**
     * Добавление комментария начала блока, анонсов-трейлеров, выключения логотипа, метки старта рекл. блока, рекламной открывашки, рекламных роликов,
     * рекламной закрывашки, метки конца рекл. блока, включения логотипа
     *
     * @param outSchedule расписание, в конец которого добавляется рекламный блок
     * @param block рекламный блок
     */
    protected void addAdBlock(List<Command> outSchedule, AdBlock block) {
        outSchedule.add(new Comment("----------- " + block.getTime(), true));
        outSchedule.add(firstTrailers.getCurrentTrailer());
        if (settings.getBoolParameter(SettingsKeys.ON_SECOND_TRAILER)) {
            outSchedule.add(secondTrailers.getCurrentTrailer());
        }

        outSchedule.addAll(adBlockDecor.getStartBlockCommands(block.getTime().getMinutes()));
        block.getMovies().forEach(adMovie -> outSchedule.add(new Movie(null, null, null, adMovie)));
        outSchedule.addAll(adBlockDecor.getEndBlockCommands(block.getTime().getMinutes()));
    }

    /**
     *
     * @param blockTime время блока
     * @return бегущую строку соответствующую времени блока ({@code null} если строка выключена)
     */
    protected TitleObjLoad getAdBlockCrawlLine(BlockTime blockTime) {
        return adBlockCrawlLine.getAdBlockCrawlLine(blockTime.getMinutes());
    }

    /**
     * Добавление коммерческой плашки
     *
     * @param outSchedule расписание, в конец которого добавляется видео с коммерческой плашкой
     * @param movie видео, на которое нужно поставить коммерческую плашку
     * @param blockTime время блока
     */
    protected void addPopUpAd(List<Command> outSchedule, Movie movie, BlockTime blockTime) {
        List<Command> popUpAdCommands = popUpAd.formPopUpAdCommands(movie, endBlockCommand.getEndBlockCommand(blockTime.getTimeInMinutes()), blockTime);
        if (popUpAdCommands != null) {
            outSchedule.addAll(popUpAdCommands);
        } else {
            outSchedule.add(getEndBlockCommand(blockTime, false));
            outSchedule.add(movie);
        }
    }

    /**
     * Поиск первого видео дальше по расписанию и проверка его на исключения.
     *
     * @param schedule расписание
     * @param index индекс текущей команды
     * @return {@code true} если видео является исключением
     */
    protected boolean checkFullExclusion(List<Command> schedule, int index) {
        if (fullExclusions != null) {
            for (int i = index; i < schedule.size(); i++) {
                Command c = schedule.get(i);
                if (CommandKey.MOVIE == c.getCommandKey()) {
                    Movie m = (Movie) c;
                    return fullExclusions.isExclusionByContains(m.getFileName());
                }
            }
        }
        return false;
    }

    /**
     * @param blockTime время рекламного блока
     * @param exclusion является ли видео, после рекламного блока, исключением
     * @return комментарий анонса-плашки или комментарий окончания рекламного блока (если плашки нет или для данного блока не нужна)
     */
    protected Command getEndBlockCommand(BlockTime blockTime, boolean exclusion) {
        if (!settings.getBoolParameter(SettingsKeys.ON_ANNOUNCEMENT) || exclusion) {
            return endBlockCommand.getEndBlockCommand();
        } else {
            return endBlockCommand.getEndBlockCommand(blockTime.getTimeInMinutes());
        }
    }

    /**
     *
     * Собирает все ошибки в один список
     */
    protected void addErrors() {
        List<BlockTime> notUsedBlocks = adBlocks.getNotUsedBlocks();
        if (!notUsedBlocks.isEmpty()) {
            notUsedBlocks.stream().forEach(e -> this.errors.add("НЕТ в Расписании Блока с временем - " + e.toString()));
        }
        List<BlockTime> notFoundBlocks = adBlocks.getNotFoundBlocks();
        if (!notFoundBlocks.isEmpty()) {
            notFoundBlocks.stream().forEach(e -> this.errors.add("НЕТ в Эфирном Листе Блока с временем - " + e.toString()));
        }
        if (settings.getBoolParameter(SettingsKeys.ON_POP_UP_AD) && !popUpAd.getPopUpAdErrors().isEmpty()) {
            this.errors.addAll(popUpAd.getPopUpAdErrors());
        }
    }

    /**
     *
     * Добавляет кол-во строк после рекламных блоков, если счетчик включен
     */
    protected void addAdditionalInfo() {
        if (settings.getBoolParameter(SettingsKeys.ON_AD_BLOCKS_CRAWLLINE_COUNTER)) {
            this.additionalInfo.put(Settings.KEY_AD_BLOCKS_CRAWL_LINE_COUNTER, String.valueOf(adBlockCrawlLine.getAdBlocksCrawlLineCounter()));
        }
    }
}
