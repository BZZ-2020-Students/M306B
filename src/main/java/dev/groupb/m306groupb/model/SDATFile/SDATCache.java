package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class SDATCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static SDATCache instance;

    private HashMap<FileDate, SDATFile[]> sdatFileHashMap = new HashMap<>();

    private SDATCache() {
    }

    public static SDATCache getInstance() {
        if (instance == null) {
            instance = new SDATCache();
        }

        return instance;
    }
}
