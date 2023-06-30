package dev.groupb.m306groupb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDate implements Comparable<FileDate> {
    private String[] fileName;
    private Date fileCreationDate;
    private Date startDate;
    private Date endDate;

    @Override
    public int compareTo(FileDate o) {
        return this.startDate.compareTo(o.startDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FileDate other = (FileDate) obj;
        return Objects.equals(startDate, other.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate);
    }
}
