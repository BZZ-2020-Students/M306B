package dev.groupb.m306groupb;

import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

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
