package xyz.pary.addblockstoplaylistcore.adblocks;

import java.util.List;

public interface AdBlocks {

    /**
     *
     * @param blockTime время рекламного блока
     * @return рекламный блок для указанного времени или @{code null} если блок не найден
     */
    public AdBlock getBlock(BlockTime blockTime);

    /**
     *
     * @param time строка со временем рекламного блока, соответствующим шаблону
     * @return рекламный блок для указанного времени или @{code null} если блок не найден
     */
    public AdBlock getBlock(String time);

    /**
     *
     * @return время не использованных блоков (есть в эфирном листе, нет в расписании)
     */
    public List<BlockTime> getNotUsedBlocks();

    /**
     *
     * @return время не найденных блоков (есть в расписании, нет в эфирном листе)
     */
    public List<BlockTime> getNotFoundBlocks();
}
