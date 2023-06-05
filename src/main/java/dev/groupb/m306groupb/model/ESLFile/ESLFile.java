package dev.groupb.m306groupb.model.ESLFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ESLFile {
    private Double highTariffConsumption;
    private Double lowTariffConsumption;
    private Double highTariffProduction;
    private Double lowTariffProduction;

    public void fillNullValues(ESLFile filler) {
        if (this.highTariffConsumption == null) {
            this.highTariffConsumption = filler.highTariffConsumption;
        }
        if (this.lowTariffConsumption == null) {
            this.lowTariffConsumption = filler.lowTariffConsumption;
        }
        if (this.highTariffProduction == null) {
            this.highTariffProduction = filler.highTariffProduction;
        }
        if (this.lowTariffProduction == null) {
            this.lowTariffProduction = filler.lowTariffProduction;
        }
    }
}
