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
public class FileDate {
    private Date fileCreationDate;
    private Date startDate;
    private Date endDate;
}
