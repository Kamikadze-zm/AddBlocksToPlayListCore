package ru.kamikadze_zm.addblockstoplaylistcore.scheduleprocessor.adblocks;

import java.text.SimpleDateFormat;
import java.util.Date;
import ru.kamikadze_zm.onair.command.TitleObjLoad;
import ru.kamikadze_zm.onair.command.parameter.ParallelDuration;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings;
import ru.kamikadze_zm.addblockstoplaylistcore.settings.Settings.SettingsKeys;

public class AdBlockCrawlLine {

    private TitleObjLoad block15CrawlLine;
    private TitleObjLoad block45CrawlLine;
    private TitleObjLoad blockOtherCrawlLine;

    private boolean isCreated = false;

    private boolean onAdBlocksCrawlLineCounter = false;
    private int adBlocksCrawlLineCounter = 0;

    public AdBlockCrawlLine(Settings settings, Date scheduleDate) {
        if (settings.getBoolParameter(SettingsKeys.ON_AD_BLOCK_CRAWL_LINE)) {
            String objectName = settings.getParameter(Settings.SettingsKeys.CRAWL_LINE_NAME);
            ParallelDuration duration = new ParallelDuration(0, 0, 0, 1);

            //формат даты для строки
            SimpleDateFormat df = new SimpleDateFormat(settings.getParameter(SettingsKeys.CRAWL_LINE_DATE_FORMAT));

            //форматирование пути к файлу задания для строки (E:\\Строка\\<> 6ТВ прочие.txt в <> подставляется отформатированная дата)
            this.block15CrawlLine = new TitleObjLoad(objectName, duration, null,
                    settings.getParameter(SettingsKeys.CRAWL_LINE_15BLOCK_PATH).replace("<>", df.format(scheduleDate)));

            this.block45CrawlLine = new TitleObjLoad(objectName, duration, null,
                    settings.getParameter(SettingsKeys.CRAWL_LINE_45BLOCK_PATH).replace("<>", df.format(scheduleDate)));

            this.blockOtherCrawlLine = new TitleObjLoad(objectName, duration, null,
                    settings.getParameter(SettingsKeys.CRAWL_LINE_OTHER_BLOCK_PATH).replace("<>", df.format(scheduleDate)));

            this.isCreated = true;
            if (settings.getBoolParameter(SettingsKeys.ON_AD_BLOCKS_CRAWLLINE_COUNTER)) {
                this.onAdBlocksCrawlLineCounter = true;
            }
        }
    }

    /**
     * Возвращает бегущую строку соответствующую времени блока (15 минутные - коммерция, 45 - лицензия, остальные - прочие) и если включено –
     * увеличивает счетчик строк
     *
     * @param minutes минутная часть времени блока
     * @return бегущую строку соответствующую времени блока ({@code null} если строка выключена)
     */
    public TitleObjLoad getAdBlockCrawlLine(int minutes) {
        if (!isCreated) {
            return null;
        }

        if (onAdBlocksCrawlLineCounter) {
            adBlocksCrawlLineCounter++;
        }

        switch (minutes) {
            case 15:
                return block15CrawlLine;
            case 45:
                return block45CrawlLine;
            default:
                return blockOtherCrawlLine;
        }
    }

    public int getAdBlocksCrawlLineCounter() {
        return adBlocksCrawlLineCounter;
    }
}
