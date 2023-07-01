package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {
    private double value;
    private EconomicActivity type;
}
