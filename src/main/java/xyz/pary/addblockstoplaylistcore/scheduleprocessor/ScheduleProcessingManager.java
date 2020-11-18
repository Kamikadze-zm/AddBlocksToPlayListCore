package xyz.pary.addblockstoplaylistcore.scheduleprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.onair.command.Command;

public class ScheduleProcessingManager {

    private static final Logger LOG = LogManager.getLogger(ScheduleProcessingManager.class);

    private List<Command> schedule;
    private final List<String> errors = new ArrayList<>();
    private final Map<String, String> additionalInfo = new HashMap<>();

    /**
     *
     * @param schedule расписание для обработки (не может быть null или пустым)
     */
    public ScheduleProcessingManager(List<Command> schedule) {
        if (schedule == null || schedule.isEmpty()) {
            String message = "Schedule cannot be null or empty";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        this.schedule = schedule;
    }

    /**
     * Обрабатывает расписание переданными обработчиками
     *
     * @param scheduleProcessors обработчики расписания
     * @return обработанное расписание
     */
    public List<Command> processSchedule(Iterable<ScheduleProcessor> scheduleProcessors) {
        if (scheduleProcessors != null) {
            for (ScheduleProcessor sp : scheduleProcessors) {
                this.schedule = sp.process(this.schedule);
                List<String> errors = sp.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    this.errors.addAll(errors);
                }
                Map<String, String> addInfo = sp.getAdditionalInfo();
                if (addInfo != null && !addInfo.isEmpty()) {
                    additionalInfo.putAll(addInfo);
                }
            }
        }
        return schedule;
    }

    /**
     *
     * @return список ошибок, если ошибок нет – пустой список
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     *
     * @return Map с дополнительной информацией, если её нет – пустой Map
     */
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
