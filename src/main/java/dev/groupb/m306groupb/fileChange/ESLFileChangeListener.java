package dev.groupb.m306groupb.fileChange;

import dev.groupb.m306groupb.model.ESLFile.ESLCache;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ESLFileChangeListener implements FileChangeListener {
    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        for (ChangedFiles cfiles : changeSet) {
            for (ChangedFile cfile : cfiles.getFiles()) {
                switch (cfile.getType()) {
                    case ADD -> {
                        System.out.println("ESL File added: " + cfile.getFile().getName());
                        ESLCache.addNewFile(cfile.getFile());
                    }
                    case DELETE -> {
                        System.out.println("ESL File deleted: " + cfile.getFile().getName());
                        ESLCache.fileRemoved(cfile.getFile().getName());
                    }
                    case MODIFY -> {
                        System.out.println("ESL File modified: " + cfile.getFile().getName());
                        ESLCache.fileChanged(cfile.getFile().getName(), cfile.getFile());
                    }
                }
            }
        }
    }
}
