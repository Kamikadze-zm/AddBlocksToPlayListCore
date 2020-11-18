package xyz.pary.addblockstoplaylistcore.adblocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pary.addblockstoplaylistcore.util.FileUtils;

public class JExcelAdBlocks implements AdBlocks {

    private static final Logger LOG = LogManager.getLogger(JExcelAdBlocks.class);

    private final static String XLS_EXT = "xls";

    private final Map<BlockTime, AdBlock> blocks = new HashMap<>();
    private final TimeFormatter timeFormater;
    private final List<BlockTime> notUsedBlocks = new ArrayList<>();
    private final List<BlockTime> notFoundBlocks = new ArrayList<>();

    /**
     *
     * @param file эфирный лист с расширением .xls
     * @param timeFormatter форматер времени рекламных блоков
     * @param adPath путь к папке с рекламными роликами
     * @throws AdSheetException при неверном расширении или в случае ошибок чтения файла
     */
    public JExcelAdBlocks(File file, TimeFormatter timeFormatter, String adPath) throws AdSheetException {
        this.timeFormater = timeFormatter;
        
        String fixedAdPath = FileUtils.fixFolderPath(adPath);

        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String ext = "";
        if (index > 0) {
            ext = fileName.substring(index + 1);
        }

        if (!ext.equalsIgnoreCase(XLS_EXT)) {
            throw new AdSheetException("Неверное расширение файла: " + fileName + ". Требуется *.xls");
        }

        Workbook wb;
        try {
            wb = Workbook.getWorkbook(file);
        } catch (IOException | BiffException e) {
            LOG.warn("Cannot read ad sheet: ", e);
            throw new AdSheetException("Не удалось прочитать файл: " + fileName);
        }

        Sheet sheet;

        try {
            sheet = wb.getSheet(0);
        } catch (IndexOutOfBoundsException e) {
            LOG.warn("Not found first sheet in file: ", e);
            throw new AdSheetException("Не найден первый лист в файле: " + fileName);
        }

        String header;

        for (int i = 0; i < sheet.getRows(); i++) {
            header = sheet.getCell(0, i).getContents();//содержимое первой ячейки в строке
            BlockTime bt = timeFormatter.stringToBlockTime(header);
            if (bt != null) {

                //проверка наличия блока
                if (blocks.containsKey(bt)) {
                    throw new AdSheetException("Эфирный лист содержит второй блок с временем: " + bt);
                }

                List<String> block = new ArrayList<>();
                i++;

                while (!sheet.getCell(2, i).getContents().isEmpty()) {
                    block.add(fixedAdPath + sheet.getCell(2, i).getContents());
                    i++;
                }
                blocks.put(bt, new AdBlock(bt, block));
                notUsedBlocks.add(bt);
                i--;
            }
        }
    }

    @Override
    public AdBlock getBlock(BlockTime blockTime) {
        AdBlock block = blocks.get(blockTime);
        if (block != null) {
            notUsedBlocks.remove(blockTime);
            return block;
        } else {
            notFoundBlocks.add(blockTime);
            return null;
        }
    }

    @Override
    public AdBlock getBlock(String time) {
        BlockTime bc = timeFormater.stringToBlockTime(time);
        if (bc != null) {
            return getBlock(bc);
        }
        return null;
    }

    @Override
    public List<BlockTime> getNotUsedBlocks() {
        return notUsedBlocks;
    }

    @Override
    public List<BlockTime> getNotFoundBlocks() {
        return notFoundBlocks;
    }

    /**
     *
     * @return используемый форматер времени рекламных блоков
     */
    public TimeFormatter getTimeFormater() {
        return timeFormater;
    }
}
