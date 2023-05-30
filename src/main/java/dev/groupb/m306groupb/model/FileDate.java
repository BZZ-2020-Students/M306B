package dev.groupb.m306groupb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDate implements Comparable<FileDate> {
    private Date fileCreationDate;
    private Date startDate;
    private Date endDate;

    @Override
    public int compareTo(FileDate o) {
        return this.startDate.compareTo(o.startDate);
    }
}
