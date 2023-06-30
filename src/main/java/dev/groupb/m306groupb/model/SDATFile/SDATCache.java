package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.enums.EconomicActivity;
import dev.groupb.m306groupb.enums.Unit;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SDATCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    private static SDATCache instance;

    private final ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = new ConcurrentHashMap<>();

    private SDATCache() {
    }

    public static SDATCache getInstance() {
        if (instance == null) {
            instance = new SDATCache();
        }

        return instance;
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
        FileDate fileDate = sdatCache.getSdatFileHashMap().keySet().stream().filter(key -> Arrays.asList(key.getFileName()).contains(fileName)).findFirst().orElse(null);

        // if the file is not found, return
        if (fileDate == null) {
            System.out.println("SDATFile not found in cache, can't remove: " + fileName);
            return;
        }

        sdatCache.getSdatFileHashMap().remove(fileDate);
    }

    public void addSDATFile(FileDate fileDate, SDATFile sdatFile) {
        updateDatesOfObservations(fileDate, sdatFile);

        SDATFile[] existing = sdatFileHashMap.get(fileDate);
        FileDate existingFileDate = sdatFileHashMap.keySet().stream().filter(key -> key.equals(fileDate)).findFirst().orElse(null);
        if (existing != null) {
            SDATFile[] newExisting = new SDATFile[existing.length + 1];
            System.arraycopy(existing, 0, newExisting, 0, existing.length);
            newExisting[existing.length] = sdatFile;

            if (existingFileDate == null) {
                System.err.println("Existing file date is null");
                sdatFileHashMap.put(fileDate, newExisting);
                return;
            }
            // add to the existingFileDate the fileName from the fileDate
            String[] newFileName = new String[existingFileDate.getFileName().length + 1];
            System.arraycopy(existingFileDate.getFileName(), 0, newFileName, 0, existingFileDate.getFileName().length);
            newFileName[existingFileDate.getFileName().length] = fileDate.getFileName()[0];
            existingFileDate.setFileName(newFileName);

            if (newFileName.length != 2 || newExisting.length != 2) {
                System.err.println("We have more than 2 files with the same date (" + fileDate.getStartDate() + "), this should not happen. Please check the files. The files are: " + Arrays.toString(newFileName) + "\nSkipping this file.");
                return;
            }
            EconomicActivity firstEconomicActivity = newExisting[0].getEconomicActivity();
            EconomicActivity secondEconomicActivity = newExisting[1].getEconomicActivity();

            if (firstEconomicActivity == secondEconomicActivity) {
                throw new RuntimeException("firstEconomicActivity == secondEconomicActivity");
            }

            sdatFileHashMap.put(existingFileDate, newExisting);
        } else {
            sdatFileHashMap.put(fileDate, new SDATFile[]{sdatFile});
        }
    }

    private void updateDatesOfObservations(FileDate fileDate, SDATFile sdatFile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fileDate.getStartDate());
        int resolution = sdatFile.getResolution().getResolution();
        Unit timeUnit = sdatFile.getResolution().getTimeUnit();

        int obsCounter = 0;
        for (Observation observation : sdatFile.getObservations()) {
            if (obsCounter >= 1) {
                if (Objects.requireNonNull(timeUnit) == Unit.MIN) {
                    calendar.add(Calendar.MINUTE, resolution);
                }
            } else {
                obsCounter++;
            }

            observation.setRelativeTime(calendar.getTime());
        }
    }
}
