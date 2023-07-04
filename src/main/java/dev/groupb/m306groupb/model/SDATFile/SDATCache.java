package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.enums.Unit;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
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

        if (sdatFile.getObservations().size() > 96) {
            System.err.println("Warning: There are more than 96 observations (there are: "+sdatFile.getObservations().size()+") in the SDATFile with the EconomicActivity " + sdatFile.getEconomicActivity() + " for this FileDate (" + fileDate.getStartDate() + "). The file name is: " + Arrays.toString(fileDate.getFileName()));

            if (sdatFile.getResolution().getResolution() == 15 && sdatFile.getResolution().getTimeUnit() == Unit.MIN) {
                System.err.println("The resolution is 15 minutes, which means that there are 96 observations in a day. This means that there are more than 1 day of observations in this file. Ignoring all observations after the first day.");

                sdatFile.getObservations().removeIf(element -> sdatFile.getObservations().headSet(element).size() >= 96);
                System.err.println("There are now " + sdatFile.getObservations().size() + " observations in the SDATFile with the EconomicActivity " + sdatFile.getEconomicActivity() + " for this FileDate (" + fileDate.getStartDate() + "). The file name is: " + Arrays.toString(fileDate.getFileName()));
            }
        }

        SDATFile[] existing = sdatFileHashMap.get(fileDate);
        FileDate existingFileDate = sdatFileHashMap.keySet().stream().filter(key -> key.equals(fileDate)).findFirst().orElse(null);
        if (existing != null) {
            // Check if the new SDATFile is a duplicate
            boolean isDuplicate = Arrays.stream(existing).anyMatch(existingSDATFile -> existingSDATFile.getEconomicActivity() == sdatFile.getEconomicActivity());
            // If it's not a duplicate, add it to the cache
            if (!isDuplicate) {
                SDATFile[] newExisting = Arrays.copyOf(existing, existing.length + 1);
                newExisting[existing.length] = sdatFile;

                if (existingFileDate == null) {
                    System.err.println("Existing file date is null");
                    sdatFileHashMap.put(fileDate, newExisting);
                    return;
                }
                // add to the existingFileDate the fileName from the fileDate
                String[] newFileName = Arrays.copyOf(existingFileDate.getFileName(), existingFileDate.getFileName().length + 1);
                newFileName[existingFileDate.getFileName().length] = fileDate.getFileName()[0];
                existingFileDate.setFileName(newFileName);

                sdatFileHashMap.put(existingFileDate, newExisting);
            } else {
                System.err.println("Warning: There is already an SDATFile with the EconomicActivity " + sdatFile.getEconomicActivity() + " for this FileDate (" + fileDate.getStartDate() + "). Cannot add another. The file name is: " + Arrays.toString(fileDate.getFileName()) + " the duplicate file name is: " + Arrays.toString(existingFileDate.getFileName()));
            }
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

            observation.setRelativeTime(calendar.getTimeInMillis());
        }
    }
}
