package ru.kamikadze_zm.addblockstoplaylistcore.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

    private static final Logger LOG = LogManager.getLogger(Settings.class);

    public static final String END_BLOCK_COMMENT = "# 16+";
    public static final String NO_OTHER_CRAWL_LINE_MARK = "noOtherCrawlLine";
    public static final String KEY_AD_BLOCKS_CRAWL_LINE_COUNTER = "adBlocksCrawlLineCounter";

    private final Map<SettingsParameter, String> settings = new HashMap<>();

    public final boolean onAdBlocks;
    public final boolean onAdMarks;
    public final boolean onAdBlockCrawlLine;
    public final boolean onTobacco;
    public final boolean onCrawlLine;
    public final boolean onAnnouncement;
    public final boolean onAnnouncerNow;
    public final boolean onPopUpAd;
    public final boolean onAllDayAnnouncements;
    public final boolean onSecondTrailer;
    public final boolean onAdBlocksCrawlLineCounter;
    public final boolean onNewsAdBlock;
    public final boolean onDifferentAdOpenersClosers;

    /**
     *
     * @param settingsFile файл настроек
     * @throws SettingsException при ошибках чтения файла или в случае отсутствующего параметра или пустого значения
     */
    public Settings(File settingsFile) throws SettingsException {
        this(loadProperties(settingsFile));
    }

    /**
     *
     * @param properties {@link Properties} c настройками
     * @throws SettingsException в случае отсутствующего параметра или пустого значения
     */
    public Settings(Properties properties) throws SettingsException {
        onAdBlocks = getBoolProperty(properties, SettingsKeys.ON_AD_BLOCKS);
        if (onAdBlocks) {
            onAdMarks = getBoolProperty(properties, SettingsKeys.ON_AD_MARKS);
            onAdBlockCrawlLine = getBoolProperty(properties, SettingsKeys.ON_AD_BLOCK_CRAWL_LINE);
            onAnnouncement = getBoolProperty(properties, SettingsKeys.ON_ANNOUNCEMENT);
            onAllDayAnnouncements = getBoolProperty(properties, SettingsKeys.ON_ALL_DAY_ANNOUNCEMENTS);
            onPopUpAd = getBoolProperty(properties, SettingsKeys.ON_POP_UP_AD);
            onAdBlocksCrawlLineCounter = getBoolProperty(properties, SettingsKeys.ON_AD_BLOCKS_CRAWLLINE_COUNTER);
            onDifferentAdOpenersClosers = getBoolProperty(properties, SettingsKeys.ON_DIFFERENT_AD_OPENERS_CLOSERS);
        } else {
            onAdMarks = false;
            onAdBlockCrawlLine = false;
            onAnnouncement = false;
            onAllDayAnnouncements = false;
            onPopUpAd = false;
            onAdBlocksCrawlLineCounter = false;
            onDifferentAdOpenersClosers = false;
        }

        onAnnouncerNow = getBoolProperty(properties, SettingsKeys.ON_ANNOUNCER_NOW);
        onCrawlLine = getBoolProperty(properties, SettingsKeys.ON_CRAWL_LINE);
        onSecondTrailer = getBoolProperty(properties, SettingsKeys.ON_SECOND_TRAILER);
        onTobacco = getBoolProperty(properties, SettingsKeys.ON_TOBACCO);
        onNewsAdBlock = getBoolProperty(properties, SettingsKeys.ON_NEWS_AD_BLOCK);

        settings.put(SettingsKeys.SCHEDULE_PATH, getProperty(properties, SettingsKeys.SCHEDULE_PATH));
        settings.put(SettingsKeys.SCHEDULE_DATE_FORMAT, getProperty(properties, SettingsKeys.SCHEDULE_DATE_FORMAT));

        if (onAdBlocks) {
            loadAdSettings(properties);
            if (onAdMarks) {
                settings.put(SettingsKeys.AD_MARK_NAME, getProperty(properties, SettingsKeys.AD_MARK_NAME));
            }
            if (onAnnouncement) {
                settings.put(SettingsKeys.ANNOUNCEMENT_MARK, getProperty(properties, SettingsKeys.ANNOUNCEMENT_MARK));
                if (onAllDayAnnouncements) {
                    settings.put(SettingsKeys.ALL_DAY_ANNOUNCEMENTS_FILE, getProperty(properties, SettingsKeys.ALL_DAY_ANNOUNCEMENTS_FILE));
                }
            }
            if (onPopUpAd) {
                loadPopUpAdSettings(properties);
            }
        }

        if (onNewsAdBlock) {
            loadNewsAdSettings(properties);
        }

        if (onAdBlocks || onNewsAdBlock) {
            loadDecorAdBlockSettings(properties);
        }

        if (onAdBlockCrawlLine || onCrawlLine) {
            loadCrawlLineSettings(properties);
        }

        if (onAdBlocks || onCrawlLine) {
            settings.put(SettingsKeys.FULL_EXCLUSIONS, getProperty(properties, SettingsKeys.FULL_EXCLUSIONS));
        }

        if (onTobacco) {
            loadTobaccoSettings(properties);
        }

        if (onAnnouncerNow) {
            settings.put(SettingsKeys.ANNOUNCER_NOW_NAME, getProperty(properties, SettingsKeys.ANNOUNCER_NOW_NAME));
            settings.put(SettingsKeys.ANNOUNCER_NOW_PATH, getProperty(properties, SettingsKeys.ANNOUNCER_NOW_PATH));
        }
    }

    /**
     * Возвращает строковое значение соответствующего параметра настроек
     *
     * @param key - название параметра
     * @return значение параметра
     */
    public String getParameter(SettingsParameter key) {
        return settings.get(key);
    }

    /**
     * Возвращает логическое значение соответствующего параметра настроек
     *
     * @param key - название параметра
     * @return значение параметра
     */
    public boolean getBoolParameter(SettingsParameter key) {
        return Boolean.parseBoolean(settings.get(key));
    }

    protected void putParameter(SettingsParameter key, String value) {
        settings.put(key, value);
    }

    private void loadAdSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.AD_SHEET_PATH, getProperty(p, SettingsKeys.AD_SHEET_PATH));
        settings.put(SettingsKeys.AD_BLOCK_TIME_FORMAT, getProperty(p, SettingsKeys.AD_BLOCK_TIME_FORMAT));
        settings.put(SettingsKeys.AD_PATH, getProperty(p, SettingsKeys.AD_PATH));
        settings.put(SettingsKeys.TRAILERS_PATH, getProperty(p, SettingsKeys.TRAILERS_PATH));
    }

    private void loadNewsAdSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.NEWS_AD_BLOCK_COMMENT, getProperty(p, SettingsKeys.NEWS_AD_BLOCK_COMMENT));
        settings.put(SettingsKeys.NEWS_AD_BLOCKS_FILE, getProperty(p, SettingsKeys.NEWS_AD_BLOCKS_FILE));
        settings.put(SettingsKeys.NEWS_AD_BLOCK_OPENER, getProperty(p, SettingsKeys.NEWS_AD_BLOCK_OPENER));
        settings.put(SettingsKeys.NEWS_AD_BLOCK_CLOSER, getProperty(p, SettingsKeys.NEWS_AD_BLOCK_CLOSER));
    }

    private void loadDecorAdBlockSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.AD_OPENER_OTHER_BLOCK, getProperty(p, SettingsKeys.AD_OPENER_OTHER_BLOCK));
        settings.put(SettingsKeys.AD_CLOSER_OTHER_BLOCK, getProperty(p, SettingsKeys.AD_CLOSER_OTHER_BLOCK));
        if (onDifferentAdOpenersClosers) {
            settings.put(SettingsKeys.AD_OPENER_15BLOCK, getProperty(p, SettingsKeys.AD_OPENER_15BLOCK));
            settings.put(SettingsKeys.AD_OPENER_45BLOCK, getProperty(p, SettingsKeys.AD_OPENER_45BLOCK));
            settings.put(SettingsKeys.AD_CLOSER_15BLOCK, getProperty(p, SettingsKeys.AD_CLOSER_15BLOCK));
            settings.put(SettingsKeys.AD_CLOSER_45BLOCK, getProperty(p, SettingsKeys.AD_CLOSER_45BLOCK));
        }
        settings.put(SettingsKeys.LOGO_NAME, getProperty(p, SettingsKeys.LOGO_NAME));
        settings.put(SettingsKeys.CLOCK_NAME, getProperty(p, SettingsKeys.CLOCK_NAME));
    }

    private void loadCrawlLineSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.CRAWL_LINE_NAME, getProperty(p, SettingsKeys.CRAWL_LINE_NAME));
        settings.put(SettingsKeys.CRAWL_LINE_15BLOCK_PATH, getProperty(p, SettingsKeys.CRAWL_LINE_15BLOCK_PATH));
        settings.put(SettingsKeys.CRAWL_LINE_45BLOCK_PATH, getProperty(p, SettingsKeys.CRAWL_LINE_45BLOCK_PATH));
        settings.put(SettingsKeys.CRAWL_LINE_OTHER_BLOCK_PATH, getProperty(p, SettingsKeys.CRAWL_LINE_OTHER_BLOCK_PATH));
        settings.put(SettingsKeys.CRAWL_LINE_OTHER_PATH, getProperty(p, SettingsKeys.CRAWL_LINE_OTHER_PATH));
        settings.put(SettingsKeys.CRAWL_LINE_DATE_FORMAT, getProperty(p, SettingsKeys.CRAWL_LINE_DATE_FORMAT));
        settings.put(SettingsKeys.CRAWL_LINE_EXCLUSIONS, getProperty(p, SettingsKeys.CRAWL_LINE_EXCLUSIONS));
    }

    private void loadTobaccoSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.TOBACCO_OBJECT_NAME, getProperty(p, SettingsKeys.TOBACCO_OBJECT_NAME));
        settings.put(SettingsKeys.TOBACCO_PATH, getProperty(p, SettingsKeys.TOBACCO_PATH));
        settings.put(SettingsKeys.TOBACCO_EXCLUSIONS, getProperty(p, SettingsKeys.TOBACCO_EXCLUSIONS));
    }

    private void loadPopUpAdSettings(Properties p) throws SettingsException {
        settings.put(SettingsKeys.POP_UP_AD_FILE, getProperty(p, SettingsKeys.POP_UP_AD_FILE));
        settings.put(SettingsKeys.POP_UP_AD_PATH, getProperty(p, SettingsKeys.POP_UP_AD_PATH));
        settings.put(SettingsKeys.POP_UP_AD_INTERVAL, getProperty(p, SettingsKeys.POP_UP_AD_INTERVAL));
        settings.put(SettingsKeys.POP_UP_AD_SHUFFLE, getProperty(p, SettingsKeys.POP_UP_AD_SHUFFLE));
    }

    private String getProperty(Properties p, SettingsKeys k) throws SettingsException {
        String prop = p.getProperty(k.getKey());
        if (prop == null) {
            LOG.error("Not found settings parameter: " + k.getKey());
            throw new SettingsException("В файле настроек отсутствует параметр: " + k.getKey());
        }
        if (prop.isEmpty()) {
            LOG.error("Not found value of settings parameter: " + k.getKey());
            throw new SettingsException("В файле настроек отсутствует значение для параметра: " + k.getKey());
        }
        return prop;
    }

    private boolean getBoolProperty(Properties p, SettingsKeys k) throws SettingsException {
        return Boolean.parseBoolean(getProperty(p, k));
    }

    private static Properties loadProperties(File settingsFile) throws SettingsException {
        Properties p = new Properties();
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(settingsFile), Charset.forName("cp1251"))) {
            p.load(is);
        } catch (FileNotFoundException e) {
            LOG.error("Settings file not found: ", e);
            throw new SettingsException("Не найден файл с настроками: " + settingsFile.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Load settings exception: ", e);
            throw new SettingsException("Ошибка загрузки файла настроек.");
        }
        return p;
    }

    /**
     * Параметры настроек
     */
    public static enum SettingsKeys implements SettingsParameter {

        /**
         * Включить вставку рекламных блоков. true - включить, false - нет
         */
        ON_AD_BLOCKS("on-ad-blocks"),
        /**
         * Включить вставку меток начала и конца рекламного блока, для таймера рекламы. true - включить, false - нет
         */
        ON_AD_MARKS("on-ad-marks"),
        /**
         * Включить вставку бегущей стрроки после рекламного блока. true - включить, false - нет
         */
        ON_AD_BLOCK_CRAWL_LINE("on-ad-block-crawl-line"),
        /**
         * Включить вставку табака. true - включить, false - нет
         */
        ON_TOBACCO("on-tobacco"),
        /**
         * Включить вставку бегущей строки - прочие. true - включить, false - нет
         */
        ON_CRAWL_LINE("on-crawl-line"),
        /**
         * Включить вставку анонсов-плашек. true - включить, false - нет
         */
        ON_ANNOUNCEMENT("on-announcement"),
        /**
         * Включить вставку задания для анонсера текущего фильма. true - включить, false - нет
         */
        ON_ANNOUNCER_NOW("on-announcer-now"),
        /**
         * Включить вставку коммерческих плашек. true - включить, false - нет
         */
        ON_POP_UP_AD("on-pop-up-ad"),
        /**
         * Включить вставку анонсов плашек на весь день. true - включить, false - нет
         */
        ON_ALL_DAY_ANNOUNCEMENTS("on-all-day-announcements"),
        /**
         * Включить вставку второго трейлера. true - включить, false - нет
         */
        ON_SECOND_TRAILER("on-second-trailer"),
        /**
         * Включить счетчик строк после рекламных блоков. true - включить, false - нет
         */
        ON_AD_BLOCKS_CRAWLLINE_COUNTER("on-ad-blocks-crawl-line-counter"),
        /**
         * Включить новостной рекламный блок. true - включить, false - нет
         */
        ON_NEWS_AD_BLOCK("on-news-ad-block"),
        /**
         * Включить разные рекламные открывашки-закрывашки для разных блоков. true - включить, false - нет
         */
        ON_DIFFERENT_AD_OPENERS_CLOSERS("on-different-ad-openers-closers"),
        /**
         * Путь к папке с рекламными роликами
         */
        AD_PATH("ad-path"),
        /**
         * Путь к файлу рекламной открывашки для 15 минутного блока
         */
        AD_OPENER_15BLOCK("ad-opener-15block"),
        /**
         * Путь к файлу рекламной открывашки для 45 минутного блока
         */
        AD_OPENER_45BLOCK("ad-opener-45block"),
        /**
         * Путь к файлу рекламной открывашки для остальных блоков (если разные открывашки-закрывашки выключены, будет использоваться эта на все блоки)
         */
        AD_OPENER_OTHER_BLOCK("ad-opener-other-block"),
        /**
         * Путь к файлу рекламной закрывашки для 15 минутного блока
         */
        AD_CLOSER_15BLOCK("ad-closer-15block"),
        /**
         * Путь к файлу рекламной закрывашки для 45 минутного блока
         */
        AD_CLOSER_45BLOCK("ad-closer-45block"),
        /**
         * Путь к файлу рекламной закрывашки для остальных блоков (если разные открывашки-закрывашки выключены, будет использоваться эта на все блоки)
         */
        AD_CLOSER_OTHER_BLOCK("ad-closer-other-block"),
        /**
         * Путь к папке для выбора расписания
         */
        SCHEDULE_PATH("schedule-path"),
        /**
         * Путь к папке для выбора эфирного листа (\\fs.settv.ru\\string\\СЭТ ТВ\\Эфирные листы\\&lt;1&gt; | yyyy\\LLLL\\)
         */
        AD_SHEET_PATH("ad-sheet-path"),
        /**
         * Формат времени рекламных блоков (hh, mm)
         */
        AD_BLOCK_TIME_FORMAT("ad-block-time-format"),
        /**
         * Формат даты в названии расписания (yyyy, MM, dd)
         */
        SCHEDULE_DATE_FORMAT("schedule-date-format"),
        /**
         * Название метки рекламного таймера
         */
        AD_MARK_NAME("ad-mark-name"),
        /**
         * Название титровального объекта - логотипа
         */
        LOGO_NAME("logo-name"),
        /**
         * Название титровального объекта - часы+погода
         */
        CLOCK_NAME("clock-name"),
        /**
         * Путь к папке с трейлерами
         */
        TRAILERS_PATH("trailers-path"),
        /**
         * Название титровального объекта - бегущей строки
         */
        CRAWL_LINE_NAME("crawl-line-name"),
        /**
         * Путь к файлу бегущей строки для 15 минутного блока
         */
        CRAWL_LINE_15BLOCK_PATH("crawl-line-15block-path"),
        /**
         * Путь к файлу бегущей строки для 45 минутного блока
         */
        CRAWL_LINE_45BLOCK_PATH("crawl-line-45block-path"),
        /**
         * Путь к файлу бегущей строки для остальных блоков
         */
        CRAWL_LINE_OTHER_BLOCK_PATH("crawl-line-other-block-path"),
        /**
         * Путь к файлу бегущей строки - прочие
         */
        CRAWL_LINE_OTHER_PATH("crawl-line-other-path"),
        /**
         * Формат даты для вставки в бегущую строку
         */
        CRAWL_LINE_DATE_FORMAT("crawl-line-date-format"),
        /**
         * Метка комментария анонса-плашки
         */
        ANNOUNCEMENT_MARK("announcement-mark"),
        /**
         * Название титровального объекта для Табака
         */
        TOBACCO_OBJECT_NAME("tobacco-object-name"),
        /**
         * Путь к файлу табака
         */
        TOBACCO_PATH("tobacco-path"),
        /**
         * Название титровального объекта - анонсера текущего фильма
         */
        ANNOUNCER_NOW_NAME("announcer-now-name"),
        /**
         * Путь к файлу заданию анонсера текущего фильма
         */
        ANNOUNCER_NOW_PATH("announcer-now-path"),
        /**
         * Путь к файлу с исключениями для табака
         */
        TOBACCO_EXCLUSIONS("tobacco-exclusions"),
        /**
         * Путь к файлу с исключениями для бегушей строки - прочие
         */
        CRAWL_LINE_EXCLUSIONS("crawl-line-exclusions"),
        /**
         * Путь к файлу с исключениями для строк, плашек (Детские программы, программы местные т.д.)
         */
        FULL_EXCLUSIONS("full-exclusions"),
        /**
         * Путь к файлу с названиями коммерческих плашек
         */
        POP_UP_AD_FILE("pop-up-ad-names-file"),
        /**
         * Путь к папке с коммерческими плашками на сервере
         */
        POP_UP_AD_PATH("pop-up-ad-path"),
        /**
         * Название титровального объекта для коммерческих плашек
         */
        POP_UP_AD_OBJECT_NAME("pop-up-ad-object-name"),
        /**
         * Промежуток между коммерческой строкой и коммерческой плашкой, в секундах
         */
        POP_UP_AD_INTERVAL("pop-up-ad-interval"),
        /**
         * Случайно перемешивать коммерческие плашки. true - перемешивать, false - нет
         */
        POP_UP_AD_SHUFFLE("pop-up-ad-shuffle"),
        /**
         * Путь к файлу с названиями анонсов-плашек на весь день
         */
        ALL_DAY_ANNOUNCEMENTS_FILE("all-day-announcements-file"),
        /**
         * Комментарий метка для вставки новостного рекламного блока
         */
        NEWS_AD_BLOCK_COMMENT("news-ad-block-comment"),
        /**
         * Путь к файлу с названиями роликов для новостного рекламного блока
         */
        NEWS_AD_BLOCKS_FILE("news-ad-block-file"),
        /**
         * Путь к файлу новостной рекламной открывашки
         */
        NEWS_AD_BLOCK_OPENER("news-ad-block-opener"),
        /**
         * Путь к файлу новостной рекламной закрывашки
         */
        NEWS_AD_BLOCK_CLOSER("news-ad-block-closer");

        private final String key;

        private SettingsKeys(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}
