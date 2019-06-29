package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.Parameters;
import ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks.EndBlockCommand;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import ru.kamikadze_zm.onair.command.Command;

public abstract class AdBlockDependentInserter extends AbstractInserter {

    private static final Logger LOG = LogManager.getLogger(AdBlockDependentInserter.class);

    protected final Parameters parameters;
    protected final EndBlockCommand endBlockCommand;

    public AdBlockDependentInserter(Settings settings, Parameters parameters) {
        super(settings);

        if (parameters == null) {
            String message = "Parameters cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        this.parameters = parameters;
        this.endBlockCommand = SharedObjects.getEndBlockCommand(settings, parameters);
    }

    /**
     * При наличии команды окончания рекламного блока перед текущей позицией вставляет указанную команду перед ней, иначе в конец расписания. Если
     * команда уже имеется на соответствующей позиции, вторая не ставится.
     *
     * @param schedule расписание
     * @param command команда для вставки
     */
    protected void checkEndAdBlockAndAddCommand(List<Command> schedule, Command command) {
        int i = -1;
        if (schedule.isEmpty() || schedule.size() - 1 < 0) {
            schedule.add(command);
            return;
        }
        if (settings.getBoolParameter(SettingsKeys.ON_AD_BLOCKS)) {
            i = endBlockCommand.getEndBlockIndex(schedule, schedule.size() - 1);
        }

        if (i == -1) {
            if (!schedule.get(schedule.size() - 1).equals(command)) {
                schedule.add(command);
            }
        } else if (!schedule.get(i - 1).equals(command)) {
            schedule.add(i, command);
        }
    }

}
