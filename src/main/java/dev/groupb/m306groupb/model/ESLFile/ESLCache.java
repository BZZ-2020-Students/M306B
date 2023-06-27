package dev.groupb.m306groupb.model.ESLFile;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.utils.ESLFileReader;
import dev.groupb.m306groupb.utils.FileReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

@Getter
public class ESLCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static ESLCache instance;

    private final HashMap<FileDate, ESLFile> eslFileMap = new HashMap<>();

    private ESLCache() {
    }

    public static void fillCacheParallel(String filesPath) {
        ESLCache eslCache = ESLCache.getInstance();
        eslCache.getEslFileMap().clear();

        File[] files = FileReader.getFiles(filesPath);

        Arrays.stream(files).parallel().forEach(ESLCache::addFileToCache);
    }

    public static ESLCache getInstance() {
        if (instance == null) {
            instance = new ESLCache();
        }
        return instance;
    }

    private static void addFileToCache(File file) {
        ESLFileReader eslFileReader = new ESLFileReader();
        int amountOfESLFilesToExpect = eslFileReader.amountOfEslFiles(file);
        for (int i = 0; i < amountOfESLFilesToExpect; i++) {
            ESLFile eslFile = eslFileReader.parseFile(file, i);
            if (eslFile == null) {
                continue;
            }
            FileDate fileDate = eslFileReader.getFileDate(file, i);
            ESLCache.getInstance().addESLFile(fileDate, eslFile);
        }
    }

    public static void fileChanged(String fileName, File file) {
        fileRemoved(fileName);
        addFileToCache(file);
    }

    public static void addNewFile(File file) {
        addFileToCache(file);
    }

    public static void fileRemoved(String fileName) {
        ESLCache eslCache = ESLCache.getInstance();

        FileDate[] fileDate = eslCache.getEslFileMap().keySet().stream().filter(key -> Arrays.asList(key.getFileName()).contains(fileName)).toArray(FileDate[]::new);

        if (fileDate.length == 0) {
            System.out.println("ESLFile not found in cache, can't remove: " + fileName);
            return;
        }

        for (FileDate date : fileDate) {
            eslCache.getEslFileMap().remove(date);
        }
    }

    public void addESLFile(FileDate fileDate, ESLFile eslFile) {
        ESLFile existing = eslFileMap.get(fileDate);
        if (existing != null) {
            existing.fillNullValues(eslFile);
        } else {
            eslFileMap.put(fileDate, eslFile);
        }
    }
}
