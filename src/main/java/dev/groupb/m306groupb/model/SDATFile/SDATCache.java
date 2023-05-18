package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

@Getter
public class SDATCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static SDATCache instance;

    private final HashMap<FileDate, SDATFile[]> sdatFileHashMap = new HashMap<>();

    private SDATCache() {
    }

    public static void fillCacheParallel(String filesPath) {
        SDATCache sdatCache = SDATCache.getInstance();
        sdatCache.getSdatFileHashMap().clear();

        SDATFileReader sdatFileReader = new SDATFileReader();
        File[] files = FileReader.getFiles(filesPath);

        Arrays.stream(files).parallel().forEach(file -> {
            SDATFile sdatFile = sdatFileReader.parseFile(file);
            FileDate fileDate = sdatFileReader.getFileDate(file);

            sdatCache.addSDATFile(fileDate, sdatFile);
        });
    }

    public static void fillCacheSequential(String filesPath) {
        SDATCache sdatCache = SDATCache.getInstance();
        sdatCache.getSdatFileHashMap().clear();

        SDATFileReader sdatFileReader = new SDATFileReader();
        File[] files = FileReader.getFiles(filesPath);

        for (File file : files) {
            SDATFile sdatFile = sdatFileReader.parseFile(file);
            FileDate fileDate = sdatFileReader.getFileDate(file);

            sdatCache.addSDATFile(fileDate, sdatFile);
        }
    }

    public static SDATCache getInstance() {
        if (instance == null) {
            instance = new SDATCache();
        }

        return instance;
    }

    public void addSDATFile(FileDate fileDate, SDATFile sdatFile) {
        SDATFile[] existing = sdatFileHashMap.get(fileDate);
        if (existing != null) {
            SDATFile[] newExisting = new SDATFile[existing.length + 1];
            System.arraycopy(existing, 0, newExisting, 0, existing.length);
            newExisting[existing.length] = sdatFile;
            sdatFileHashMap.put(fileDate, newExisting);
        } else {
            sdatFileHashMap.put(fileDate, new SDATFile[]{sdatFile});
        }
    }
}
