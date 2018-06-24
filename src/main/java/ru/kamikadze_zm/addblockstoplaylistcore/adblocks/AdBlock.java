package ru.kamikadze_zm.addblockstoplaylistcore.adblocks;

import java.util.List;

public class AdBlock {

    private final BlockTime time;
    private final List<String> movies;

    public AdBlock(BlockTime blockTime, List<String> movies) {
        this.time = blockTime;
        this.movies = movies;
    }

    public BlockTime getTime() {
        return time;
    }

    public List<String> getMovies() {
        return movies;
    }
}
