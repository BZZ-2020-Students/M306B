package dev.groupb.m306groupb;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component
public class Initializer implements CommandLineRunner {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @Override
    public void run(String... args) {
        SDATCache sdatCache = SDATCache.getInstance();

        SDATFileReader sdatFileReader = new SDATFileReader();
        File[] files = FileReader.getFiles(sdat_files_path);
        HashMap<FileDate, SDATFile[]> sdatFileHashMap = new HashMap<>();

        int amountFilesToLoad = files.length;
        int amountFilesLoaded = 0;
        for (File file : files) {
            SDATFile sdatFile = sdatFileReader.parseFile(file);
            FileDate fileDate = sdatFileReader.getFileDate(file);

            SDATFile[] existing = sdatFileHashMap.get(fileDate);
            if (existing != null) {
                SDATFile[] newExisting = new SDATFile[existing.length + 1];
                System.arraycopy(existing, 0, newExisting, 0, existing.length);
                newExisting[existing.length] = sdatFile;
                sdatFileHashMap.put(fileDate, newExisting);
            } else {
                sdatFileHashMap.put(fileDate, new SDATFile[]{sdatFile});
            }

            amountFilesLoaded++;
            System.out.println("Loading SDAT files: " + amountFilesLoaded + "/" + amountFilesToLoad + " (" + (int) ((double) amountFilesLoaded / amountFilesToLoad * 100) + "%)");
        }

        sdatCache.setSdatFileHashMap(sdatFileHashMap);
    }
}
