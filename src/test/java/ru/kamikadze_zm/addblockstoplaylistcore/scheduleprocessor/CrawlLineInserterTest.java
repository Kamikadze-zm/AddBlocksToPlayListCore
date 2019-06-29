package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks.EndBlockCommand;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Movie;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.parameter.ParallelDuration;

public class CrawlLineInserterTest {

    /**
     * Test of process method, of class CrawlLineInserter.
     */
    @Test
    public void testProcess() {
        Date scheduleDate = new Date();
        EndBlockCommand endBlockCommand = new EndBlockCommand();
        String clName = "cl";
        ParallelDuration clDuration = new ParallelDuration(0, 0, 0, 1);
        String clPath = "path";
        String dateFormat = "yyyyMMdd";
        Exclusions crawlLineExclusions = new Exclusions();
        crawlLineExclusions.add("exclusion");
        Exclusions fullExclusions = new Exclusions();
        CrawlLineInserter crawlLineInserter0Parts = new CrawlLineInserter(
                scheduleDate,
                endBlockCommand,
                clName,
                clDuration,
                clPath,
                dateFormat,
                crawlLineExclusions, fullExclusions, 0);
        CrawlLineInserter crawlLineInserter1Parts = new CrawlLineInserter(
                scheduleDate,
                endBlockCommand,
                clName,
                clDuration,
                clPath,
                dateFormat,
                crawlLineExclusions, fullExclusions, 1);
        CrawlLineInserter crawlLineInserter2Parts = new CrawlLineInserter(
                scheduleDate,
                endBlockCommand,
                clName,
                clDuration,
                clPath,
                dateFormat,
                crawlLineExclusions, fullExclusions, 2);

        TitleObjLoad crawlLine = new TitleObjLoad(clName, clDuration, null, clPath);

        List<Command> schedule = new ArrayList<>(19);
        schedule.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie 00:00:00.01 exclusion"));
        schedule.add(new Movie("movie 00:00:00.01 exclusion"));
        schedule.add(endBlockCommand.getEndBlockCommand());
        schedule.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        schedule.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));

        schedule.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        schedule.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));

        List<Command> sch0parts = crawlLineInserter0Parts.process(schedule);
        List<Command> exp0parts = new ArrayList<>(32);
        exp0parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp0parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp0parts.add(endBlockCommand.getEndBlockCommand());
        exp0parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));

        exp0parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp0parts.add(crawlLine);
        exp0parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        compare(exp0parts, sch0parts);

        List<Command> sch1parts = crawlLineInserter1Parts.process(schedule);
        List<Command> exp1parts = new ArrayList<>(27);
        exp1parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp1parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp1parts.add(endBlockCommand.getEndBlockCommand());
        exp1parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));

        exp1parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp1parts.add(crawlLine);
        exp1parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        compare(exp1parts, sch1parts);

        List<Command> sch2parts = crawlLineInserter2Parts.process(schedule);
        List<Command> exp2parts = new ArrayList<>(27);
        exp2parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp2parts.add(new Movie("movie 00:00:00.01 exclusion"));
        exp2parts.add(endBlockCommand.getEndBlockCommand());
        exp2parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));
        exp2parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_59.mpeg"));

        exp2parts.add(new Movie("movie <00:00:00.00> 00:04:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:04:28.29> 00:04:00.00 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(new Movie("movie <00:08:28.29> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(new Movie("movie <00:16:56.58> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:25:24.87> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(new Movie("movie <00:33:53.17> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(new Movie("movie <00:42:21.46> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        exp2parts.add(crawlLine);
        exp2parts.add(new Movie("movie <00:50:49.75> 00:08:28.29 E:\\Июнь\\16+\\Метод Лавровой_60.mpeg"));
        compare(exp2parts, sch2parts);
    }

    private void compare(List<Command> expectedSchedule, List<Command> actualSchedule) {
        if (expectedSchedule.size() != actualSchedule.size()) {
            for (int i = 0; i < min(expectedSchedule.size(), actualSchedule.size()); i++) {
                System.out.println(expectedSchedule.get(i).toSheduleRow() + "      " + actualSchedule.get(i).toSheduleRow());
            }
        }
        assertEquals(expectedSchedule.size(), actualSchedule.size());
        for (int i = 0; i < expectedSchedule.size(); i++) {
            assertEquals(expectedSchedule.get(i).toSheduleRow(), actualSchedule.get(i).toSheduleRow());
        }
    }
}
