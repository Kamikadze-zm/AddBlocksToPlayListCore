package xyz.pary.addblockstoplaylistcore.scheduleprocessor;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.addblockstoplaylistcore.Parameters;
import xyz.pary.addblockstoplaylistcore.scheduleprocessor.adblocks.AdBlockDecor;
import xyz.pary.addblockstoplaylistcore.scheduleprocessor.adblocks.EndBlockCommand;
import xyz.pary.addblockstoplaylistcore.settings.Settings;
import xyz.pary.addblockstoplaylistcore.settings.Settings.SettingsKeys;

public class SharedObjects {

    private static final Logger LOG = LogManager.getLogger(SharedObjects.class);

    private static AdBlockDecor adBlockDecor;
    private static EndBlockCommand endBlockCommand;

    private static Exclusions fullExclusions;
    private static boolean exclusionsError;

    private SharedObjects() {
    }

    public static AdBlockDecor getAdBlockDecor(Settings settings) {
        if (adBlockDecor == null) {
            adBlockDecor = new AdBlockDecor(settings);
        }
        return adBlockDecor;
    }

    public static EndBlockCommand getEndBlockCommand(Settings settings, Parameters parameters) {
        if (endBlockCommand == null) {
            endBlockCommand = new EndBlockCommand(settings, parameters);
        }
        return endBlockCommand;
    }

    public static Exclusions getFullExclusions(Settings settings) {
        if (fullExclusions == null && !exclusionsError) {
            if (settings.getParameter(SettingsKeys.FULL_EXCLUSIONS) != null) {
                try {
                    fullExclusions = new Exclusions(settings.getParameter(SettingsKeys.FULL_EXCLUSIONS));
                } catch (IOException e) {
                    LOG.warn("Cannot read full exclusions file: ", e);
                    exclusionsError = true;
                }
            }
        }
        return fullExclusions;
    }
}
