package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SDATFileWithDate {
    private SDATFile[] SDATFiles;
    private FileDate fileDate;
}
