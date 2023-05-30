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

    public void fillNullValues(ESLFile filler) {
        if (this.highTariffConsumption == 0) {
            this.highTariffConsumption = filler.highTariffConsumption;
        }
        if (this.lowTariffConsumption == 0) {
            this.lowTariffConsumption = filler.lowTariffConsumption;
        }
        if (this.highTariffProduction == 0) {
            this.highTariffProduction = filler.highTariffProduction;
        }
        if (this.lowTariffProduction == 0) {
            this.lowTariffProduction = filler.lowTariffProduction;
        }
    }
}
