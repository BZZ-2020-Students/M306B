package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
public class SDATCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static SDATCache instance;

    private final HashMap<FileDate, SDATFile[]> sdatFileHashMap = new HashMap<>();

    private SDATCache() {
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

    public static SDATCache getInstance() {
        if (instance == null) {
            instance = new SDATCache();
        }
        return instance;
    }
}
