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
    private Double highTariffConsumption;
    private Double lowTariffConsumption;
    private Double highTariffProduction;
    private Double lowTariffProduction;
}
