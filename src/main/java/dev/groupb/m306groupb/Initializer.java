package dev.groupb.m306groupb;

import dev.groupb.m306groupb.model.ESLFile.ESLCache;
import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.utils.ESLFileReader;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
public class Initializer implements CommandLineRunner {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @Override
    public void run(String... args) {
        System.out.println("Loading SDAT files...");
        SDATCache.fillCacheParallel(sdat_files_path);
        System.out.println("All SDAT files processed and loaded!");
 
        // ESL Files init
        ESLCache eslCache = ESLCache.getInstance();

        ESLFileReader eslFileReader = new ESLFileReader();
        File[] ESLFiles = FileReader.getFiles(esl_files_path);

        int ESLamountFilesToLoad = ESLFiles.length;
        int ESLamountFilesLoaded = 0;
        for (File file : ESLFiles) {
            ESLFile eslFile = eslFileReader.parseFile(file);
            FileDate fileDate = eslFileReader.getFileDate(file);

            eslCache.addESLFile(fileDate, eslFile);

            ESLamountFilesLoaded++;
            System.out.println("Loading ESL files: " + ESLamountFilesLoaded + "/" + ESLamountFilesToLoad + " (" + (int) ((double) ESLamountFilesLoaded / ESLamountFilesToLoad * 100) + "%)");
        }
    }
}
