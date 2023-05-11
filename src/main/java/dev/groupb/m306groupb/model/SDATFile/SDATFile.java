package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.model.FileType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SDATFile {
    private String fileName;
    private String filePath;
    private FileType fileType;
}
