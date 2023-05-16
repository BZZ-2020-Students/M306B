package dev.groupb.m306groupb.model.SDATFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.enums.MeasureUnit;
import dev.groupb.m306groupb.model.Resolution;
import lombok.Builder;
import lombok.Data;

import java.util.SortedSet;

@Data
@Builder
public class SDATFile {
    private String fileName;
    @JsonIgnore
    private String filePath;
    private FileType fileType;
    private Resolution resolution;
    private MeasureUnit measureUnit;
    private SortedSet<Observation> observations;
}