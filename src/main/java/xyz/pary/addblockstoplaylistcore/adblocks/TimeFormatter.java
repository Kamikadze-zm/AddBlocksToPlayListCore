package xyz.pary.addblockstoplaylistcore.adblocks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatter {

    private final Pattern timePattern;
    private final String delimiter;

    /**
     *
     * @param timeFormat формат времени рекламных блоков (HH - часы, mm - минуты)
     */
    public TimeFormatter(String timeFormat) {
        String normalizedTimeFormat = timeFormat.trim().toLowerCase();
        this.timePattern = Pattern.compile("^" + normalizedTimeFormat.replace("hh", "(\\d\\d|\\d)").replaceAll("[hm]", "\\\\d"));
        this.delimiter = normalizedTimeFormat.substring(2, normalizedTimeFormat.length() - 2);
    }

    /**
     *
     * @return разделитель частей времени (часов и минут)
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     *
     * @param time строка со временем, соответствующим шаблону
     * @return время блока, если строка не соответствует шаблону – {@code null}
     */
    public BlockTime stringToBlockTime(String time) {
        String normalizedTime = time.trim().toLowerCase();
        Matcher matcher = timePattern.matcher(normalizedTime);
        if (matcher.find()) {
            String foundTime = normalizedTime.substring(matcher.start(), matcher.end());
            String[] timeParts = foundTime.split(delimiter);
            return new BlockTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), delimiter);
        }
        return null;
    }
}
