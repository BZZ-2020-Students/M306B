package dev.groupb.m306groupb.model.ESLFile;

import dev.groupb.m306groupb.model.FileDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ESLFileWithDate implements Comparable<ESLFileWithDate>{
    private ESLFile ESLFile;
    private FileDate fileDate;

    @Override
    public int compareTo(ESLFileWithDate o) {
        return this.fileDate.compareTo(o.fileDate);
    }
}
