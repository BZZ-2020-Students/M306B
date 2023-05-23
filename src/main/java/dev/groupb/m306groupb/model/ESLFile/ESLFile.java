package dev.groupb.m306groupb.model.ESLFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ESLFile {
    private String fileName;
    @JsonIgnore
    private String filePath;
    private double highTariffConsumption;
    private double lowTariffConsumption;
    private double highTariffProduction;
    private double lowTariffProduction;
}
