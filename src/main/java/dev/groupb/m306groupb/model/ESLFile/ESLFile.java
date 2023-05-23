package dev.groupb.m306groupb.model.ESLFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ESLFile {
    private double highTariffConsumption;
    private double lowTariffConsumption;
    private double highTariffProduction;
    private double lowTariffProduction;
}
