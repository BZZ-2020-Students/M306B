package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SDATCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    private static SDATCache instance;

    private final ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = new ConcurrentHashMap<>();

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

    private static void addFileToCache(File file) {
        SDATFileReader sdatFileReader = new SDATFileReader();
        SDATFile sdatFile = sdatFileReader.parseFile(file);
        FileDate fileDate = sdatFileReader.getFileDate(file);

        SDATCache.getInstance().addSDATFile(fileDate, sdatFile);
    }

    public static void fileChanged(String fileName, File newFile) {
        fileRemoved(fileName);
        addFileToCache(newFile);
    }

    public static void addNewFile(File file) {
        addFileToCache(file);
    }

    public static void fileRemoved(String fileName) {
        SDATCache sdatCache = SDATCache.getInstance();

        // find the file in the cache by looking at the file name
        FileDate fileDate = sdatCache.getSdatFileHashMap().keySet().stream().filter(fileDate1 -> fileDate1.getFileName().equals(fileName)).findFirst().orElse(null);

        // if the file is not found, return
        if (fileDate == null) {
            System.out.println("File not found in cache: " + fileName);
            return;
        }

        sdatCache.getSdatFileHashMap().remove(fileDate);
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
