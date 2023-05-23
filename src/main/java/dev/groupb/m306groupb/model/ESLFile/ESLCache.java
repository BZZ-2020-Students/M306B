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
        ESLCache sdatCache = ESLCache.getInstance();
        sdatCache.getEslFileMap().clear();

        ESLFileReader eslFileReader = new ESLFileReader();
        File[] files = FileReader.getFiles(filesPath);

        Arrays.stream(files).parallel().forEach(file -> {
            ESLFile sdatFile = eslFileReader.parseFile(file);
            FileDate fileDate = eslFileReader.getFileDate(file);

            sdatCache.addESLFile(fileDate, sdatFile);
        });
    }

    public static ESLCache getInstance() {
        if (instance == null) {
            instance = new ESLCache();
        }
        return instance;
    }

    public void addESLFile(FileDate fileDate, ESLFile eslFile) {
        eslFileMap.put(fileDate, eslFile);
    }
}
