package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {
    private double value;
    private EconomicActivity type;
}
