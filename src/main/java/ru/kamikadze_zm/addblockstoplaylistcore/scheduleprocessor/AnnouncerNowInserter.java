package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.parameter.Duration;

public class AnnouncerNowInserter extends AbstractInserter {

    private static final Logger LOG = LogManager.getLogger(AnnouncerNowInserter.class);

    private Command announcerNow;

    public AnnouncerNowInserter(Settings settings, Date scheduleDate) {
        super(settings);

        if (scheduleDate == null) {
            String message = "ScheduleDate cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        if (!settings.getBoolParameter(SettingsKeys.ON_ANNOUNCER_NOW)) {
            String message = "AnnouncerNowInserter cannot be used, when setting " + SettingsKeys.ON_ANNOUNCER_NOW.getKey() + " is not true";
            LOG.error(message);
            throw new IllegalStateException(message);
        }

        this.announcerNow = new TitleObjLoad(settings.getParameter(SettingsKeys.ANNOUNCER_NOW_NAME), new Duration(), null,
                settings.getParameter(SettingsKeys.ANNOUNCER_NOW_PATH)
                .replace("<>", new SimpleDateFormat(settings.getParameter(SettingsKeys.SCHEDULE_DATE_FORMAT)).format(scheduleDate)));
    }

    @Override
    public List<Command> process(List<Command> schedule) {
        if (!schedule.get(1).equals(announcerNow)) {
            schedule.add(1, announcerNow);
        }
        return schedule;
    }

    public void setAnnouncerNow(Command announcerNow) {
        this.announcerNow = announcerNow;
    }
}
