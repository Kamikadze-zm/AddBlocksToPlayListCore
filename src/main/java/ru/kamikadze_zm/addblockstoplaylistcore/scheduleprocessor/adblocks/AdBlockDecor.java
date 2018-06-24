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
 * Содержит два списка команд, для начала (команды выключения часов, выключения логотипа, метки начала рекламного блока (если включена), рекламной
 * открывашки) и конца (команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов) блока
 */
public class AdBlockDecor {

    private final List<Command> startBlockCommands;
    private final List<Command> endBlockCommands;
    
    private final List<Command> startBlockCommandsWithoutAdMark;
    private final List<Command> endBlockCommandsWithoutAdMark;

    public AdBlockDecor(Settings settings) {

        String logoName = settings.getParameter(SettingsKeys.LOGO_NAME);
        Duration logoDuration = new Duration(0, 0, 0, 4);
        Fade logoFade = new Fade(0.04);
        TitleObjOn logoOn = new TitleObjOn(logoName, logoDuration, logoFade);
        TitleObjOff logoOff = new TitleObjOff(logoName, logoDuration, logoFade);
        String clockName = settings.getParameter(SettingsKeys.CLOCK_NAME);
        TitleObjOn clockOn = new TitleObjOn(clockName, logoDuration, logoFade);
        TitleObjOff clockOff = new TitleObjOff(clockName, logoDuration, logoFade);

        Duration adOpenerCloserDuration = new Duration(0, 0, 4, 0);
        Movie adOpener = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_OPENER));
        Movie adCloser = new Movie(null, adOpenerCloserDuration, null, settings.getParameter(SettingsKeys.AD_CLOSER));

        startBlockCommands = new ArrayList<>(4);
        endBlockCommands = new ArrayList<>(4);
        
        startBlockCommandsWithoutAdMark = new ArrayList<>(3);
        endBlockCommandsWithoutAdMark = new ArrayList<>(3);

        startBlockCommands.add(clockOff);
        startBlockCommands.add(logoOff);
        endBlockCommands.add(adCloser);
        
        startBlockCommandsWithoutAdMark.add(clockOff);
        startBlockCommandsWithoutAdMark.add(logoOff);
        endBlockCommandsWithoutAdMark.add(adCloser);

        if (settings.onAdMarks) {
            String markName = settings.getParameter(SettingsKeys.AD_MARK_NAME);
            MarkStart adStartMark = new MarkStart(markName, "Начало рекламного блока");
            MarkStop adEndMark = new MarkStop(markName, "Конец рекламного блока");

            startBlockCommands.add(adStartMark);
            endBlockCommands.add(adEndMark);
        }

        startBlockCommands.add(adOpener);
        endBlockCommands.add(logoOn);
        endBlockCommands.add(clockOn);
        
        startBlockCommandsWithoutAdMark.add(adOpener);
        endBlockCommandsWithoutAdMark.add(logoOn);
        endBlockCommandsWithoutAdMark.add(clockOn);
    }

    /**
     *
     * @return команды выключения часов, выключения логотипа, метки начала рекламного блока (если включена), рекламной открывашки
     */
    public List<Command> getStartBlockCommands() {
        return startBlockCommands;
    }

    /**
     *
     * @return команды рекламной закрывашки, метки конца рекламного блока (если включена), включения логотипа, включения часов
     */
    public List<Command> getEndBlockCommands() {
        return endBlockCommands;
    }
    
    /**
     *
     * @return команды выключения часов, выключения логотипа, рекламной открывашки (без метки начала рекламного блока, даже если включена)
     */
    public List<Command> getStartBlockCommandsWithoutAdMark() {
        return startBlockCommandsWithoutAdMark;
    }

    /**
     *
     * @return команды рекламной закрывашки, включения логотипа, включения часов (без метки конца рекламного блока, даже если включена)
     */
    public List<Command> getEndBlockCommandsWithoutAdMark() {
        return endBlockCommandsWithoutAdMark;
    }
}
