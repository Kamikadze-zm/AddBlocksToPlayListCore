package xyz.pary.addblockstoplaylistcore.scheduleprocessor;

import java.util.List;
import java.util.Map;
import xyz.pary.onair.command.Command;

public interface ScheduleProcessor {

    /**
     *
     * @param schedule расписание
     * @return обработанное расписание
     */
    public List<Command> process(List<Command> schedule);

    /**
     *
     * @return список ошибок, если ошибок нет – пустой список
     */
    public List<String> getErrors();

    /**
     *
     * @return Map с дополнительной информацией, если её нет – пустой Map
     */
    public Map<String, String> getAdditionalInfo();
}
