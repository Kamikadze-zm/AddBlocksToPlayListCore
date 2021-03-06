package xyz.pary.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.addblockstoplaylistcore.scheduleprocessor.AbstractInserter;
import xyz.pary.addblockstoplaylistcore.scheduleprocessor.SharedObjects;
import xyz.pary.addblockstoplaylistcore.settings.Settings;
import xyz.pary.addblockstoplaylistcore.settings.Settings.SettingsKeys;
import xyz.pary.addblockstoplaylistcore.util.FileUtils;
import xyz.pary.onair.command.Command;
import xyz.pary.onair.command.Command.CommandKey;
import xyz.pary.onair.command.Comment;
import xyz.pary.onair.command.Movie;

public class NewsAdBlocksInserter extends AbstractInserter {

    private static final Logger LOG = LogManager.getLogger(NewsAdBlocksInserter.class);

    private final AdBlockDecor adBlockDecor;
    private final String newsAdBlockMark;
    private List<String> newsAdBlockMovies;

    public NewsAdBlocksInserter(Settings settings) {
        super(settings);
        if (!settings.getBoolParameter(SettingsKeys.ON_NEWS_AD_BLOCK)) {
            String message = "NewsAdBlocksInserter cannot be used, when setting " + SettingsKeys.ON_NEWS_AD_BLOCK.getKey() + " is not true";
            LOG.error(message);
            throw new IllegalStateException(message);
        }

        this.newsAdBlockMark = settings.getParameter(SettingsKeys.NEWS_AD_BLOCK_COMMENT).trim().toLowerCase();
        try {
            this.newsAdBlockMovies = FileUtils.getLinesFromFile(settings.getParameter(SettingsKeys.NEWS_AD_BLOCKS_FILE));
        } catch (IOException e) {
            LOG.warn("Cannot read news ad block file: ", e);
        }

        this.adBlockDecor = SharedObjects.getAdBlockDecor(settings);
    }

    @Override
    public List<Command> process(List<Command> schedule) {
        List<Command> outSchedule = new ArrayList<>();

        for (Command command : schedule) {

            if (CommandKey.COMMENT == command.getCommandKey()) {
                String c = ((Comment) command).getComment();

                if (c.trim().toLowerCase().startsWith(newsAdBlockMark)) {
                    outSchedule.add(new Comment("--- " + c, true));
                    outSchedule.addAll(adBlockDecor.getStartNewsBlockCommands());
                    if (newsAdBlockMovies != null && !newsAdBlockMovies.isEmpty()) {
                        newsAdBlockMovies.forEach(adMovie -> outSchedule.add(new Movie(null, null, null, adMovie)));
                    }
                    outSchedule.addAll(adBlockDecor.getEndNewsBlockCommands());
                    outSchedule.add(new Comment("", true));
                } else {
                    outSchedule.add(command);
                }
            } else {
                outSchedule.add(command);
            }
        }
        return outSchedule;
    }
}
