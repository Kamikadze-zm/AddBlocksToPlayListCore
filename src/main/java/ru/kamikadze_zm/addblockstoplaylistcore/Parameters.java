package ru.kamikadze_zm.addblockstoplaylistcore;

import java.io.Serializable;

/**
 * Параметры по умолчанию: trailersNumber = 2; secondTrailersNumber = 2; announceName = ""; announcementStartTime = 0; announcementEndTime = 0,
 * commerceCrawlLineDuration = 120 (в секундах);
 */
public class Parameters implements Serializable {

    private int trailersNumber = 2;
    private int secondTrailersNumber = 2;
    private String announcementName = "";
    private int announcementStartTime = 0;
    private int announcementEndTime = 0;
    private int commerceCrawlLineDuration = 120;

    /**
     *
     * @return кол-во трейлеров
     */
    public int getTrailersNumber() {
        return trailersNumber;
    }

    /**
     *
     * @param trailersNumber кол-во трейлеров, значение меньшее или равное нулю будет проигнорировано
     */
    public void setTrailersNumber(int trailersNumber) {
        if (trailersNumber > 0) {
            this.trailersNumber = trailersNumber;
        }
    }

    /**
     *
     * @return кол-во вторых трейлеров
     */
    public int getSecondTrailersNumber() {
        return secondTrailersNumber;
    }

    /**
     *
     * @param secondTrailersNumber кол-во вторых трейлеров, значение меньшее или равное нулю будет проигнорировано
     */
    public void setSecondTrailersNumber(int secondTrailersNumber) {
        if (secondTrailersNumber > 0) {
            this.secondTrailersNumber = secondTrailersNumber;
        }
    }

    /**
     *
     * @return название анонса-плашки
     */
    public String getAnnouncementName() {
        return announcementName;
    }

    /**
     *
     * @param announcementName название анонса-плашки
     */
    public void setAnnouncementName(String announcementName) {
        if (announcementName != null) {
            this.announcementName = announcementName;
        } else {
            this.announcementName = "";
        }
    }

    /**
     *
     * @return время старта анонса-плашки, в минутах
     */
    public int getAnnouncementStartTime() {
        return announcementStartTime;
    }

    /**
     *
     * @param announcementStartTime время старта анонса-плашки, в минутах
     */
    public void setAnnouncementStartTime(int announcementStartTime) {
        this.announcementStartTime = announcementStartTime;
    }

    /**
     *
     * @return время окончания анонса-плашки, в минутах
     */
    public int getAnnouncementEndTime() {
        return announcementEndTime;
    }

    /**
     *
     * @param announcementEndTime время окончания анонса-плашки, в минутах
     */
    public void setAnnouncementEndTime(int announcementEndTime) {
        this.announcementEndTime = announcementEndTime;
    }

    /**
     *
     * @return длительность коммерческой бегущей строки, в секундах
     */
    public int getCommerceCrawlLineDuration() {
        return commerceCrawlLineDuration;
    }

    /**
     *
     * @param commerceCrawlLineDuration длительность коммерческой бегущей строки, в секундах
     */
    public void setCommerceCrawlLineDuration(int commerceCrawlLineDuration) {
        if (commerceCrawlLineDuration > 10) {
            this.commerceCrawlLineDuration = commerceCrawlLineDuration;
        } else {
            this.commerceCrawlLineDuration = 120;
        }
    }
}
