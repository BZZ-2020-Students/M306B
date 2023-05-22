package dev.groupb.m306groupb;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.utils.FileReader;
import dev.groupb.m306groupb.utils.SDATFileReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;

@SpringBootTest
class InitializerTest {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @Test
    void loadSDATFilesSequential() {
        // get current time
        long startTime = System.currentTimeMillis();

        // Fill cache
        SDATCache.fillCacheSequential(sdat_files_path);

        // get current time
        long endTime = System.currentTimeMillis();

        // calculate time difference
        long timeElapsed = endTime - startTime;

        System.out.println("Normal Execution time in milliseconds: " + timeElapsed);
    }

    @Test
    void loadSDATFilesParallel() {
        // get current time
        long startTime = System.currentTimeMillis();

        // Fill cache
        SDATCache.fillCacheParallel(sdat_files_path);

        // get current time
        long endTime = System.currentTimeMillis();

        // calculate time difference
        long timeElapsed = endTime - startTime;

        System.out.println("Parallel Execution time in milliseconds: " + timeElapsed);
    }
}
