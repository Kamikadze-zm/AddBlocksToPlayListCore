package xyz.pary.addblockstoplaylistcore.scheduleprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.addblockstoplaylistcore.settings.EmptySettings;
import xyz.pary.addblockstoplaylistcore.settings.Settings;

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

    /**
     * Настройкам присваивается {@link EmptySettings}
     */
    protected AbstractInserter() {
        this.settings = new EmptySettings();
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
