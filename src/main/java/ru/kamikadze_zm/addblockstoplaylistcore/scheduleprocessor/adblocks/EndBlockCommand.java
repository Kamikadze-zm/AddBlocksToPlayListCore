package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.Parameters;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.addblockstoplaylistcore.util.FileUtils;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Command.CommandKey;
import ru.kamikadze_zm.onair.command.Comment;

public class EndBlockCommand {

    private static final Logger LOG = LogManager.getLogger(EndBlockCommand.class);

    private String announcementMark;

    private Command announcement;
    private int announcementStartTime;
    private int announcementEndTime;

    private List<Command> allDayAnnouncements;
    private int currentAllDayAnnouncement = 0;
    private boolean nowAllDay = false;

    private final Comment endBlockCommand;

    public EndBlockCommand(Settings settings, Parameters parameters) {
        if (settings.onAnnouncement && !parameters.getAnnouncementName().isEmpty()) {

            this.announcementMark = settings.getParameter(SettingsKeys.ANNOUNCEMENT_MARK);

            this.announcement = new Comment(announcementMark + " " + parameters.getAnnouncementName(), true);
            this.announcementStartTime = parameters.getAnnouncementStartTime();
            this.announcementEndTime = parameters.getAnnouncementEndTime();

            if (settings.onAllDayAnnouncements) {
                try {
                    List<String> announcementNames = FileUtils.getLinesFromFile(settings.getParameter(SettingsKeys.ALL_DAY_ANNOUNCEMENTS_FILE));
                    this.allDayAnnouncements = new ArrayList<>();
                    announcementNames.stream().forEach(name -> this.allDayAnnouncements
                            .add(new Comment(announcementMark + " " + name, true)));
                } catch (IOException e) {
                    LOG.warn("Cannot read all day announcements file: ", e);
                }
            }
        }
        //комментарий окончания рекламного блока, где отсутствует анонс-плашка
        this.endBlockCommand = new Comment(Settings.END_BLOCK_COMMENT, true);
    }

    /**
     * Возвращает комментарий анонса-плашки или комментарий окончания рекламного блока
     *
     * @param blockTimeInMins время рекламного блока в минутах
     * @return комментарий анонса-плашки, если для указанного блока плашки нет – комментарий окончания рекламного блока
     */
    public Command getEndBlockCommand(int blockTimeInMins) {
        if (allDayAnnouncements != null && !allDayAnnouncements.isEmpty()) {
            if (!nowAllDay && checkAnnouncement(blockTimeInMins)) {
                nowAllDay = true;
                return announcement;
            } else {
                nowAllDay = false;
                return getAllDayAnnouncement();
            }
        } else if (checkAnnouncement(blockTimeInMins)) {
            return announcement;
        }
        return getEndBlockCommand();
    }

    /**
     * @return комментарий окончания рекламного блока
     */
    public Command getEndBlockCommand() {
        return endBlockCommand;
    }

    private Command getAllDayAnnouncement() {
        Command allDayAnnouncement = allDayAnnouncements.get(currentAllDayAnnouncement);
        currentAllDayAnnouncement++;
        if (currentAllDayAnnouncement == allDayAnnouncements.size()) {
            currentAllDayAnnouncement = 0;
        }
        return allDayAnnouncement;
    }

    /**
     *
     * @param blockTimeInMins время блока в минутах
     * @return {@code true} если есть название анонса и время текущего блока входит в заданный интервал
     */
    private boolean checkAnnouncement(int blockTimeInMins) {
        return (announcement != null && blockTimeInMins >= announcementStartTime && blockTimeInMins <= announcementEndTime);
    }

    /**
     * Ищет комментарий анонса-плашки вверх по расписанию или комментарий окончания рекламного блока. Пропускает только пустые комментарии
     *
     * @param schedule расписание
     * @param currentIndex индекс текущей команды
     * @return индекс анонса-плашки или {@link Settings#END_BLOCK_COMMENT}, если плашка и комментарий окончания блока отсутствуют, возвращает -1
     */
    public int getEndBlockIndex(List<Command> schedule, int currentIndex) {
        if (rangeCheck(currentIndex, schedule.size())) {
            //поиск комментария с анонсом-плашкой
            int i = currentIndex;
            while (CommandKey.COMMENT == schedule.get(i).getCommandKey()) {
                Comment comment = (Comment) schedule.get(i);
                if (isEndBlock(comment)) {
                    return i;
                } else if (!comment.getComment().isEmpty()) { //может быть комментарий под новости
                    break;
                } else {
                    i--;
                    if (!rangeCheck(i, schedule.size())) {
                        break;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Проверяет является ли указанная команда концом рекламного блока
     *
     * @param command проверяемая команда
     * @return @{code true} если команда является концом рекламного блока, иначе {@code false}
     */
    public boolean isEndBlock(Command command) {
        if (CommandKey.COMMENT != command.getCommandKey()) {
            return false;
        }
        Comment c = (Comment) command;
        return c.getComment().equals(endBlockCommand.getComment())
                || (announcementMark != null && c.getComment().startsWith(announcementMark));
    }

    private boolean rangeCheck(int index, int size) {
        return index >= 0 && index < size;
    }
}
