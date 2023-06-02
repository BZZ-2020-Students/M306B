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
    private String fileName;
    private Date fileCreationDate;
    private Date startDate;
    private Date endDate;

    @Override
    public int compareTo(FileDate o) {
        return this.startDate.compareTo(o.startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDate fileDate = (FileDate) o;

        if (getFileCreationDate() != null ? !getFileCreationDate().equals(fileDate.getFileCreationDate()) : fileDate.getFileCreationDate() != null)
            return false;
        if (getStartDate() != null ? !getStartDate().equals(fileDate.getStartDate()) : fileDate.getStartDate() != null)
            return false;
        return getEndDate() != null ? getEndDate().equals(fileDate.getEndDate()) : fileDate.getEndDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getFileCreationDate() != null ? getFileCreationDate().hashCode() : 0;
        result = 31 * result + (getStartDate() != null ? getStartDate().hashCode() : 0);
        result = 31 * result + (getEndDate() != null ? getEndDate().hashCode() : 0);
        return result;
    }
}
