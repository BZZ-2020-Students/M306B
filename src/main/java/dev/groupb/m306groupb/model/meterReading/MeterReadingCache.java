package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.Observation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class MeterReadingCache {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static MeterReadingCache instance;
    private double consumptionValue;
    private double productionvalue;
    private List<Double> observationValues;

    private final ConcurrentHashMap<FileDate, MeterReading> observationHashMap = new ConcurrentHashMap<>();

    private MeterReadingCache() {
    }

    public static MeterReadingCache getInstance() {
        if (instance == null) {
            instance = new MeterReadingCache();
        }

        return instance;
    }
}
