package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeterReading {
    private double value;
    private EconomicActivity type;

    public MeterReading() {
    }
}
