package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;

public abstract class AbstractInserter implements ScheduleProcessor {

    private static final Logger LOG = LogManager.getLogger(AbstractInserter.class);

    protected final Settings settings;
    protected final List<String> errors = new ArrayList<>();
    protected final Map<String, String> additionalInfo = new HashMap<>();

    public AbstractInserter(Settings settings) {
        if (settings == null) {
            String message = "Settings cannot be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        this.settings = settings;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
