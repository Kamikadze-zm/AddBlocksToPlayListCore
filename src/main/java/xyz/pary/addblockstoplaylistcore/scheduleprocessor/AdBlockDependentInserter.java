package xyz.pary.addblockstoplaylistcore.scheduleprocessor;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.addblockstoplaylistcore.Parameters;
import xyz.pary.addblockstoplaylistcore.scheduleprocessor.adblocks.EndBlockCommand;
import xyz.pary.addblockstoplaylistcore.settings.Settings;
import xyz.pary.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import xyz.pary.onair.command.Command;

public abstract class AdBlockDependentInserter extends AbstractInserter {

    private static final Logger LOG = LogManager.getLogger(AdBlockDependentInserter.class);

    protected final EndBlockCommand endBlockCommand;

    public AdBlockDependentInserter(Settings settings, Parameters parameters) {
        super(settings);

        if (parameters == null) {
            String message = "Parameters cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        this.endBlockCommand = SharedObjects.getEndBlockCommand(settings, parameters);
    }

    /**
     * Создает AdBlockDependentInserter с пустыми настройками
     *
     * @param endBlockCommand команда окончания рекламного блока
     */
    protected AdBlockDependentInserter(EndBlockCommand endBlockCommand) {
        super();

        if (endBlockCommand == null) {
            String message = "EndBlockCommand cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        this.endBlockCommand = endBlockCommand;
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
