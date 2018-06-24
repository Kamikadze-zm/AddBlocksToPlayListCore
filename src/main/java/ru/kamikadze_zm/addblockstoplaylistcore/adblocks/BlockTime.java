package ru.kamikadze_zm.addblockstoplaylistcore.adblocks;

import java.util.Objects;

public class BlockTime {

    private static final String DEFAULT_DELIMITER = ":";

    private final Integer hours;
    private final Integer minutes;
    private final Integer timeInMinutes;
    private final String delimiter;

    public BlockTime(Integer hours, Integer minutes) {
        this(hours, minutes, DEFAULT_DELIMITER);
    }

    public BlockTime(Integer hours, Integer minutes, String delimiter) {
        this.hours = hours;
        this.minutes = minutes;
        this.timeInMinutes = hours * 60 + minutes;
        this.delimiter = delimiter;
    }

    public BlockTime(Integer timeInMinutes) {
        this(timeInMinutes, DEFAULT_DELIMITER);
    }

    public BlockTime(Integer timeInMinutes, String delimiter) {
        this.timeInMinutes = timeInMinutes;
        this.hours = timeInMinutes / 60;
        this.minutes = timeInMinutes % 60;
        this.delimiter = delimiter;
    }

    public Integer getHours() {
        return hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.timeInMinutes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockTime other = (BlockTime) obj;
        if (!Objects.equals(this.timeInMinutes, other.timeInMinutes)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return время в формате: hhDmm, где D – разделитель
     */
    @Override
    public String toString() {
        return String.format("%2$02d%1$s%3$02d", delimiter, hours, minutes);
    }
}
