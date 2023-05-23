package dev.groupb.m306groupb.fileChange;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Duration;

@Configuration
public class FileWatcherConfig {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @Bean
    public FileSystemWatcher fileSystemWatcherSDAT() {
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, Duration.ofMillis(2000L), Duration.ofMillis(1000L));
        fileSystemWatcher.addSourceDirectory(new File(sdat_files_path));
        fileSystemWatcher.addListener(new SDATFileChangeListener());
        fileSystemWatcher.setTriggerFilter(pathname -> pathname.getName().endsWith(".xml"));
        fileSystemWatcher.start();
        System.out.println("started SDAT fileSystemWatcher");
        return fileSystemWatcher;
    }

    @Bean
    public FileSystemWatcher fileSystemWatcherESL() {
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, Duration.ofMillis(2000L), Duration.ofMillis(1000L));
        fileSystemWatcher.addSourceDirectory(new File(esl_files_path));
        fileSystemWatcher.addListener(new ESLFileChangeListener());
        fileSystemWatcher.setTriggerFilter(pathname -> pathname.getName().endsWith(".xml"));
        fileSystemWatcher.start();
        System.out.println("started ESL fileSystemWatcher");
        return fileSystemWatcher;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        fileSystemWatcherSDAT().stop();
        fileSystemWatcherESL().stop();
    }
}
