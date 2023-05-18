package dev.groupb.m306groupb.model.ESLFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.security.auth.callback.TextInputCallback;
import java.util.*;

@Getter
public class ESLCache {
    // Lombok ignore setter and getter
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static ESLCache instance;

    private final HashMap<FileDate, ESLFile> eslFileMap = new HashMap<>();

    private ESLCache() {

    }

    public void addESLFile(FileDate fileDate, ESLFile eslFile) {
        eslFileMap.put(fileDate, eslFile);
    }

    public static ESLCache getInstance() {
        if (instance == null) {
            instance = new ESLCache();
        }
        return instance;
    }
}
