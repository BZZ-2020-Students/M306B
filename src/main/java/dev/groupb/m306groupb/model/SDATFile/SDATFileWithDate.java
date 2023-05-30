package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SDATFileWithDate implements Comparable<SDATFileWithDate> {
    private SDATFile[] SDATFiles;
    private FileDate fileDate;

    @Override
    public int compareTo(SDATFileWithDate o) {
        return this.fileDate.compareTo(o.fileDate);
    }
}
