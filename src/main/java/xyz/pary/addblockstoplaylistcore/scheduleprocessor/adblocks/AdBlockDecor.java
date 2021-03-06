package xyz.pary.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xyz.pary.onair.command.Command;
import xyz.pary.onair.command.MarkStart;
import xyz.pary.onair.command.MarkStop;
import xyz.pary.onair.command.Movie;
import xyz.pary.onair.command.TitleObjOff;
import xyz.pary.onair.command.TitleObjOn;
import xyz.pary.command.parameter.Duration;
import xyz.pary.command.parameter.Fade;
import xyz.pary.addblockstoplaylistcore.settings.Settings;
import xyz.pary.addblockstoplaylistcore.settings.Settings.SettingsKeys;

/**
 * Содержит команды, для начала (команды выключения часов, выключения логотипа, метки начала рекламного блока (если включена), рекламной открывашки) и
 * конца (команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов) блока
 */
public class AdBlockDecor {

    private final TitleObjOn logoOn;
    private final TitleObjOff logoOff;
    private final TitleObjOn clockOn;
    private final TitleObjOff clockOff;

    private final boolean onDifferentAdOpenersClosers;
    private final Movie adOpener15block;
    private final Movie adOpener45block;
    private final Movie adOpenerOtherBlock;
    private final Movie adCloser15block;
    private final Movie adCloser45block;
    private final Movie adCloserOtherBlock;

    private final boolean onAdMarks;
    private final MarkStart adStartMark;
    private final MarkStop adEndMark;

    private final boolean onNewsAdBlock;
    private final Movie newsAdBlockOpener;
    private final Movie newsAdBlockCloser;

    public AdBlockDecor(Settings settings) {

        String logoName = settings.getParameter(SettingsKeys.LOGO_NAME);
        Duration logoDuration = new Duration(0, 0, 0, 4);
        Fade logoFade = new Fade(0.04);
        this.logoOn = new TitleObjOn(logoName, logoDuration, logoFade);
        this.logoOff = new TitleObjOff(logoName, logoDuration, logoFade);
        String clockName = settings.getParameter(SettingsKeys.CLOCK_NAME);
        this.clockOn = new TitleObjOn(clockName, logoDuration, logoFade);
        this.clockOff = new TitleObjOff(clockName, logoDuration, logoFade);

        this.onDifferentAdOpenersClosers = settings.getBoolParameter(SettingsKeys.ON_DIFFERENT_AD_OPENERS_CLOSERS);
        Duration adOpenerCloserDuration = new Duration(0, 0, 4, 0);
        if (this.onDifferentAdOpenersClosers) {
            this.adOpener15block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_15BLOCK));
            this.adOpener45block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_45BLOCK));
            this.adCloser15block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_CLOSER_15BLOCK));
            this.adCloser45block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_CLOSER_45BLOCK));
        } else {
            this.adOpener15block = null;
            this.adOpener45block = null;
            this.adCloser15block = null;
            this.adCloser45block = null;
        }
        this.adOpenerOtherBlock = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_OTHER_BLOCK));
        this.adCloserOtherBlock = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_CLOSER_OTHER_BLOCK));

        this.onAdMarks = settings.getBoolParameter(SettingsKeys.ON_AD_MARKS);
        if (this.onAdMarks) {
            String markName = settings.getParameter(SettingsKeys.AD_MARK_NAME);
            this.adStartMark = new MarkStart(markName, "Начало рекламного блока");
            this.adEndMark = new MarkStop(markName, "Конец рекламного блока");
        } else {
            this.adStartMark = null;
            this.adEndMark = null;
        }

        this.onNewsAdBlock = settings.getBoolParameter(SettingsKeys.ON_NEWS_AD_BLOCK);
        if (this.onNewsAdBlock) {
            this.newsAdBlockOpener = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.NEWS_AD_BLOCK_OPENER));
            this.newsAdBlockCloser = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.NEWS_AD_BLOCK_CLOSER));
        } else {
            this.newsAdBlockOpener = null;
            this.newsAdBlockCloser = null;
        }
    }

    /**
     *
     * @param minutes минутная часть времени рекламного блока
     * @return команды выключения часов, выключения логотипа, метки начала рекламного блока (если включена), рекламной открывашки
     */
    public List<Command> getStartBlockCommands(int minutes) {
        List<Command> startBlockCommands = new ArrayList<>(4);
        startBlockCommands.add(clockOff);
        startBlockCommands.add(logoOff);
        if (onAdMarks) {
            startBlockCommands.add(adStartMark);
        }
        if (onDifferentAdOpenersClosers) {
            switch (minutes) {
                case 15:
                    startBlockCommands.add(adOpener15block);
                    break;
                case 45:
                    startBlockCommands.add(adOpener45block);
                    break;
                default:
                    startBlockCommands.add(adOpenerOtherBlock);
                    break;
            }
        } else {
            startBlockCommands.add(adOpenerOtherBlock);
        }
        return startBlockCommands;
    }

    /**
     * @param minutes минутная часть времени рекламного блока
     * @return команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов
     */
    public List<Command> getEndBlockCommands(int minutes) {
        List<Command> endBlockCommands = new ArrayList<>(4);
        if (onDifferentAdOpenersClosers) {
            switch (minutes) {
                case 15:
                    endBlockCommands.add(adCloser15block);
                    break;
                case 45:
                    endBlockCommands.add(adCloser45block);
                    break;
                default:
                    endBlockCommands.add(adCloserOtherBlock);
                    break;
            }
        } else {
            endBlockCommands.add(adCloserOtherBlock);
        }
        if (onAdMarks) {
            endBlockCommands.add(adEndMark);
        }
        endBlockCommands.add(logoOn);
        endBlockCommands.add(clockOn);
        return endBlockCommands;
    }

    /**
     *
     * @return команды выключения часов, выключения логотипа, рекламной открывашки (без метки начала рекламного блока, даже если включена)
     */
    public List<Command> getStartNewsBlockCommands() {
        if (!this.onNewsAdBlock) {
            return Collections.emptyList();
        }
        List<Command> startNewsBlockCommands = new ArrayList<>(3);
        startNewsBlockCommands.add(clockOff);
        startNewsBlockCommands.add(logoOff);
        startNewsBlockCommands.add(newsAdBlockOpener);
        return startNewsBlockCommands;
    }

    /**
     *
     * @return команды рекламной закрывашки, включения логотипа, включения часов (без метки конца рекламного блока, даже если включена)
     */
    public List<Command> getEndNewsBlockCommands() {
        if (!this.onNewsAdBlock) {
            return Collections.emptyList();
        }
        List<Command> endNewsBlockCommands = new ArrayList<>(3);
        endNewsBlockCommands.add(newsAdBlockCloser);
        endNewsBlockCommands.add(logoOn);
        endNewsBlockCommands.add(clockOn);
        return endNewsBlockCommands;
    }
}
