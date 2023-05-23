package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.model.FileDate;

import java.io.File;

public interface FileReader<T> {
    static File[] getFiles(String path) {
        File folder = new File(path);
        return folder.listFiles();
    }

    FileDate getFileDate(File file);

    T parseFile(File file);
}
