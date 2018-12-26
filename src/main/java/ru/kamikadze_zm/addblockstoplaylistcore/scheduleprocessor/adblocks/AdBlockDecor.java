package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.util.ArrayList;
import java.util.List;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.MarkStart;
import ru.kamikadze_zm.onair.command.MarkStop;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjOff;
import ru.kamikadze_zm.onair.command.TitleObjOn;
import ru.kamikadze_zm.onair.command.parameter.Duration;
import ru.kamikadze_zm.onair.command.parameter.Fade;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;

/**
 * Содержит команды, для начала (команды выключения часов, выключения логотипа, метки начала рекламного блока (если включена), рекламной открывашки) и
 * конца (команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов) блока
 */
public class AdBlockDecor {

    private final TitleObjOn logoOn;
    private final TitleObjOff logoOff;
    private final TitleObjOn clockOn;
    private final TitleObjOff clockOff;

    private final boolean onDifferentAdOpeners;
    private final Movie adOpener15block;
    private final Movie adOpener45block;
    private final Movie adOpenerOtherBlock;
    private final Movie adCloser;

    private final boolean onAdMarks;
    private final MarkStart adStartMark;
    private final MarkStop adEndMark;

    public AdBlockDecor(Settings settings) {

        String logoName = settings.getParameter(SettingsKeys.LOGO_NAME);
        Duration logoDuration = new Duration(0, 0, 0, 4);
        Fade logoFade = new Fade(0.04);
        this.logoOn = new TitleObjOn(logoName, logoDuration, logoFade);
        this.logoOff = new TitleObjOff(logoName, logoDuration, logoFade);
        String clockName = settings.getParameter(SettingsKeys.CLOCK_NAME);
        this.clockOn = new TitleObjOn(clockName, logoDuration, logoFade);
        this.clockOff = new TitleObjOff(clockName, logoDuration, logoFade);

        this.onDifferentAdOpeners = settings.onDifferentAdOpeners;
        Duration adOpenerCloserDuration = new Duration(0, 0, 4, 0);
        if (this.onDifferentAdOpeners) {
            this.adOpener15block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_15BLOCK));
            this.adOpener45block = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_45BLOCK));
        } else {
            this.adOpener15block = null;
            this.adOpener45block = null;
        }
        this.adOpenerOtherBlock = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER_OTHER_BLOCK));
        this.adCloser = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_CLOSER));

        this.onAdMarks = settings.onAdMarks;
        if (settings.onAdMarks) {
            String markName = settings.getParameter(SettingsKeys.AD_MARK_NAME);
            this.adStartMark = new MarkStart(markName, "Начало рекламного блока");
            this.adEndMark = new MarkStop(markName, "Конец рекламного блока");
        } else {
            this.adStartMark = null;
            this.adEndMark = null;
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
        if (onDifferentAdOpeners) {
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
     *
     * @return команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов
     */
    public List<Command> getEndBlockCommands() {
        List<Command> endBlockCommands = new ArrayList<>(4);
        endBlockCommands.add(adCloser);
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
        List<Command> startNewsBlockCommands = new ArrayList<>(3);
        startNewsBlockCommands.add(clockOff);
        startNewsBlockCommands.add(logoOff);
        startNewsBlockCommands.add(adOpenerOtherBlock);
        return startNewsBlockCommands;
    }

    /**
     *
     * @return команды рекламной закрывашки, включения логотипа, включения часов (без метки конца рекламного блока, даже если включена)
     */
    public List<Command> getEndNewsBlockCommands() {
        List<Command> endNewsBlockCommands = new ArrayList<>(3);
        endNewsBlockCommands.add(adCloser);
        endNewsBlockCommands.add(logoOn);
        endNewsBlockCommands.add(clockOn);
        return endNewsBlockCommands;
    }
}
