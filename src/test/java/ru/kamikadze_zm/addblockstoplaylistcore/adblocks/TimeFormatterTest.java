package ru.kamikadze_zm.addblockstoplaylistcore.adblocks;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.BlockTime;
import ru.kamikadze_zm.addblockstoplaylistcore.adblocks.TimeFormatter;

public class TimeFormatterTest {

    private static final String DEFAULT_DELIMITER = ":";
    private static final String TWO_SYMBOLS_DELIMITER = "dd";

    private TimeFormatter defaultTimeFormatter;
    private TimeFormatter twoSymbolsDelimiterTimeFormatter;

    @Before
    public void setUp() {
        defaultTimeFormatter = new TimeFormatter("HH" + DEFAULT_DELIMITER + "mm");
        twoSymbolsDelimiterTimeFormatter = new TimeFormatter("HH" + TWO_SYMBOLS_DELIMITER + "mm");
    }

    @Test
    public void testReceivingDelimiter() {
        assertEquals(DEFAULT_DELIMITER, defaultTimeFormatter.getDelimiter());
        assertEquals(TWO_SYMBOLS_DELIMITER, twoSymbolsDelimiterTimeFormatter.getDelimiter());
    }

    @Test
    public void testStringToBlockTime() {
        String timeDD = "08" + DEFAULT_DELIMITER + "15";
        String timeTSD = "08" + TWO_SYMBOLS_DELIMITER + "15";
        BlockTime blockTimeDD = new BlockTime(8, 15, DEFAULT_DELIMITER);
        BlockTime blockTimeTSD = new BlockTime(8, 15, TWO_SYMBOLS_DELIMITER);
        
        assertEquals(blockTimeDD, defaultTimeFormatter.stringToBlockTime(timeDD));
        assertNull(defaultTimeFormatter.stringToBlockTime(timeTSD));
        assertEquals(blockTimeTSD, twoSymbolsDelimiterTimeFormatter.stringToBlockTime(timeTSD));
        assertNull(twoSymbolsDelimiterTimeFormatter.stringToBlockTime(timeDD));
    }
}
