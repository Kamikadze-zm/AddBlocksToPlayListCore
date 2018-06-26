package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.kamikadze_zm.addblockstoplaylistcore.Parameters;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.SettingsException;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.WaitOperator;
import ru.kamikadze_zm.onair.command.parameter.Duration;
import ru.kamikadze_zm.onair.command.parameter.MarkIn;
import ru.kamikadze_zm.onair.command.parameter.ParallelDuration;

public class AdBlockDependentInserterTest {

    private AdBlockDependentInserter adBlockDependentInserter;
    private Command endBlockCommand;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.put(SettingsKeys.ON_AD_BLOCKS.getKey(), "true");
        properties.put(SettingsKeys.ON_ANNOUNCEMENT.getKey(), "false");

        properties.put(SettingsKeys.ON_AD_MARKS.getKey(), "false");
        properties.put(SettingsKeys.ON_AD_BLOCK_CRAWL_LINE.getKey(), "false");
        properties.put(SettingsKeys.ON_ALL_DAY_ANNOUNCEMENTS.getKey(), "false");
        properties.put(SettingsKeys.ON_POP_UP_AD.getKey(), "false");
        properties.put(SettingsKeys.ON_AD_BLOCKS_CRAWLLINE_COUNTER.getKey(), "false");
        properties.put(SettingsKeys.ON_ANNOUNCER_NOW.getKey(), "false");
        properties.put(SettingsKeys.ON_CRAWL_LINE.getKey(), "false");
        properties.put(SettingsKeys.ON_SECOND_TRAILER.getKey(), "false");
        properties.put(SettingsKeys.ON_TOBACCO.getKey(), "false");
        properties.put(SettingsKeys.ON_NEWS_AD_BLOCK.getKey(), "false");
        properties.put(SettingsKeys.SCHEDULE_PATH.getKey(), "path");
        properties.put(SettingsKeys.SCHEDULE_DATE_FORMAT.getKey(), "format");
        properties.put(SettingsKeys.AD_SHEET_PATH.getKey(), "path");
        properties.put(SettingsKeys.AD_BLOCK_TIME_FORMAT.getKey(), "format");
        properties.put(SettingsKeys.AD_PATH.getKey(), "path");
        properties.put(SettingsKeys.TRAILERS_PATH.getKey(), "path");
        properties.put(SettingsKeys.FULL_EXCLUSIONS.getKey(), "path");
        properties.put(SettingsKeys.AD_OPENER.getKey(), "path");
        properties.put(SettingsKeys.AD_CLOSER.getKey(), "path");
        properties.put(SettingsKeys.LOGO_NAME.getKey(), "name");
        properties.put(SettingsKeys.CLOCK_NAME.getKey(), "name");
        Settings settings;
        try {
            settings = new Settings(properties);
        } catch (SettingsException e) {
            throw new RuntimeException(e.getMessage());
        }
        Parameters parameters = new Parameters();
        adBlockDependentInserter = new AdBlockDependentInserterImpl(settings, parameters);
        endBlockCommand = SharedObjects.getEndBlockCommand(settings, parameters).getEndBlockCommand();
    }

    /**
     * Test of checkEndAdBlockAndAddCommand method, of class AdBlockDependentInserter.
     */
    @Test
    public void testCheckEndAdBlockAndAddCommand() {
        Command startCommand = new WaitOperator("", true);
        Command movie1part1 = new Movie(new MarkIn(0, 0, 0, 0), new Duration(0, 10, 0, 0), null, "movie1");
        Command movie1part2 = new Movie(new MarkIn(0, 10, 0, 0), new Duration(0, 10, 0, 0), null, "movie1");
        Command movie2part1 = new Movie(new MarkIn(0, 0, 0, 0), new Duration(0, 10, 0, 0), null, "movie2");
        Command movie2part2 = new Movie(new MarkIn(0, 10, 0, 0), new Duration(0, 10, 0, 0), null, "movie2");
        Command tobacco = new TitleObjLoad("tobacco", new ParallelDuration(0, 0, 5, 0), null, "path");

        List<Command> scheduleWithoutEndBlockCommand = new ArrayList<>();
        scheduleWithoutEndBlockCommand.add(startCommand);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithoutEndBlockCommand, tobacco);
        scheduleWithoutEndBlockCommand.add(movie1part1);
        scheduleWithoutEndBlockCommand.add(movie1part2);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithoutEndBlockCommand, tobacco);
        scheduleWithoutEndBlockCommand.add(movie2part1);
        scheduleWithoutEndBlockCommand.add(movie2part2);

        List<Command> expectedScheduleWithoutEndBlockCommand = new ArrayList<>();
        expectedScheduleWithoutEndBlockCommand.add(startCommand);
        expectedScheduleWithoutEndBlockCommand.add(tobacco);
        expectedScheduleWithoutEndBlockCommand.add(movie1part1);
        expectedScheduleWithoutEndBlockCommand.add(movie1part2);
        expectedScheduleWithoutEndBlockCommand.add(tobacco);
        expectedScheduleWithoutEndBlockCommand.add(movie2part1);
        expectedScheduleWithoutEndBlockCommand.add(movie2part2);
        compare(expectedScheduleWithoutEndBlockCommand, scheduleWithoutEndBlockCommand);

        List<Command> scheduleWithEndBlockCommand = new ArrayList<>();
        scheduleWithEndBlockCommand.add(startCommand);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithEndBlockCommand, tobacco);
        scheduleWithEndBlockCommand.add(movie1part1);
        scheduleWithEndBlockCommand.add(movie1part2);
        scheduleWithEndBlockCommand.add(endBlockCommand);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithEndBlockCommand, tobacco);
        scheduleWithEndBlockCommand.add(movie2part1);
        scheduleWithEndBlockCommand.add(movie2part2);

        List<Command> expectedScheduleWithEndBlockCommand = new ArrayList<>();
        expectedScheduleWithEndBlockCommand.add(startCommand);
        expectedScheduleWithEndBlockCommand.add(tobacco);
        expectedScheduleWithEndBlockCommand.add(movie1part1);
        expectedScheduleWithEndBlockCommand.add(movie1part2);
        expectedScheduleWithEndBlockCommand.add(tobacco);
        expectedScheduleWithEndBlockCommand.add(endBlockCommand);
        expectedScheduleWithEndBlockCommand.add(movie2part1);
        expectedScheduleWithEndBlockCommand.add(movie2part2);
        compare(expectedScheduleWithEndBlockCommand, scheduleWithEndBlockCommand);

        List<Command> scheduleWithEndBlockCommandAndInsertableCommandPresent = new ArrayList<>();
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(startCommand);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(tobacco);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithEndBlockCommandAndInsertableCommandPresent, tobacco);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie1part1);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie1part2);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(tobacco);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(endBlockCommand);
        adBlockDependentInserter.checkEndAdBlockAndAddCommand(scheduleWithEndBlockCommandAndInsertableCommandPresent, tobacco);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie2part1);
        scheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie2part2);

        List<Command> expectedScheduleWithEndBlockCommandAndInsertableCommandPresent = new ArrayList<>();
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(startCommand);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(tobacco);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie1part1);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie1part2);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(tobacco);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(endBlockCommand);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie2part1);
        expectedScheduleWithEndBlockCommandAndInsertableCommandPresent.add(movie2part2);
        compare(expectedScheduleWithEndBlockCommandAndInsertableCommandPresent, scheduleWithEndBlockCommandAndInsertableCommandPresent);

    }

    private void compare(List<Command> expectedSchedule, List<Command> actualSchedule) {
        assertEquals(expectedSchedule.size(), actualSchedule.size());
        for (int i = 0; i < expectedSchedule.size(); i++) {
            assertEquals(expectedSchedule.get(i).toSheduleRow(), actualSchedule.get(i).toSheduleRow());
        }
    }

    private class AdBlockDependentInserterImpl extends AdBlockDependentInserter {

        public AdBlockDependentInserterImpl(Settings settings, Parameters parameters) {
            super(settings, parameters);
        }

        @Override
        public List<Command> process(List<Command> schedule) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
