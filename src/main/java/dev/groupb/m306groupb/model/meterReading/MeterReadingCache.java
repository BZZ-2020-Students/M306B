package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;
import dev.groupb.m306groupb.model.ESLFile.ESLCache;
import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.ESLFile.ESLFileWithDate;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.Observation;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.model.SDATFile.SDATFileWithDate;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MeterReadingCache {
    @Getter(AccessLevel.NONE)
    private static MeterReadingCache instance;

    private final ConcurrentHashMap<Date, MeterReading[]> observationHashMap = new ConcurrentHashMap<>();

    private MeterReadingCache() {
    }

    public static MeterReadingCache getInstance() {
        if (instance == null) {
            instance = new MeterReadingCache();
        }

        return instance;
    }

    public static void fillCacheParallel() {
        SDATCache sdatCache = SDATCache.getInstance();
        ESLCache eslCache = ESLCache.getInstance();
        MeterReadingCache meterReadingCache = MeterReadingCache.getInstance();
        meterReadingCache.getObservationHashMap().clear();

        ConcurrentHashMap<FileDate, ESLFile> eslFileHashMap = eslCache.getEslFileMap();
        // sort by start date
        List<ESLFileWithDate> fileDateESLFilesList = new ArrayList<>(eslFileHashMap.entrySet().stream()
                .map(fileDateEntry -> ESLFileWithDate
                        .builder()
                        .fileDate(fileDateEntry.getKey())
                        .ESLFile(fileDateEntry.getValue())
                        .build())
                .toList());
        fileDateESLFilesList.sort(ESLFileWithDate::compareTo);

        ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = sdatCache.getSdatFileHashMap();
        // sort by start date
        List<SDATFileWithDate> fileDateSdatFilesList = new java.util.ArrayList<>(sdatFileHashMap.entrySet().stream().parallel()
                .map(entry -> SDATFileWithDate.builder().fileDate(entry.getKey()).SDATFiles(entry.getValue()).build())
                .toList());
        fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

        int eslIndex = 0;
        int sdatIndex = 0;
        ESLFile currentESLFile = fileDateESLFilesList.get(eslIndex).getESLFile();
        SDATFile[] currentSDATFiles = fileDateSdatFilesList.get(sdatIndex).getSDATFiles();

        double currentConsumptionValue = 0;
        double currentProductionValue = 0;

        // Iterate over the SDATFiles and ESLFiles
        while (sdatIndex < fileDateSdatFilesList.size() && eslIndex < fileDateESLFilesList.size()) {
            // Check if the current SDATFile comes before the current ESLFile
            if (fileDateSdatFilesList.get(sdatIndex).getFileDate().getStartDate().before(fileDateESLFilesList.get(eslIndex).getFileDate().getStartDate())) {
                // Process the current SDATFile
                for (SDATFile currentSDATFile : currentSDATFiles) {
                    for (Observation observation : currentSDATFile.getObservations()) {
                        // Calculate the absolute value for this observation
                        switch (currentSDATFile.getEconomicActivity()) {
                            case Consumption -> currentConsumptionValue += observation.getVolume();
                            case Production -> currentProductionValue += observation.getVolume();
                            default ->
                                    throw new IllegalStateException("Unexpected value: " + currentSDATFile.getEconomicActivity());
                        }
                        // Store the absolute values in the cache
                        Date date = new Date(observation.getRelativeTime());
                        MeterReading[] meterReadings = meterReadingCache.getObservationHashMap().get(date);
                        if (meterReadings == null) {
                            meterReadings = new MeterReading[2];
                            meterReadings[0] = MeterReading.builder().type(EconomicActivity.Consumption).build();
                            meterReadings[1] = MeterReading.builder().type(EconomicActivity.Production).build();
                            meterReadingCache.getObservationHashMap().put(date, meterReadings);
                        }
                        meterReadings[0].setValue(currentConsumptionValue);
                        meterReadings[1].setValue(currentProductionValue);
                    }
                }
                // Move to the next SDATFile
                sdatIndex++;
                if (sdatIndex < fileDateSdatFilesList.size()) {
                    currentSDATFiles = fileDateSdatFilesList.get(sdatIndex).getSDATFiles();
                }
            } else {
                // Process the current ESLFile
                // Reset the values and use the ESL file values as absolute values / base for the next iterations
                currentConsumptionValue = currentESLFile.getHighTariffConsumption() + currentESLFile.getLowTariffConsumption();
                currentProductionValue = currentESLFile.getHighTariffProduction() + currentESLFile.getLowTariffProduction();
                // Move to the next ESLFile
                eslIndex++;
                if (eslIndex < fileDateESLFilesList.size()) {
                    currentESLFile = fileDateESLFilesList.get(eslIndex).getESLFile();
                }
            }
        }

        // Process any remaining SDATFiles
        while (sdatIndex < fileDateSdatFilesList.size()) {
            // Process the current SDATFile
            for (SDATFile currentSDATFile : currentSDATFiles) {
                for (Observation observation : currentSDATFile.getObservations()) {
                    // Calculate the absolute value for this observation
                    switch (currentSDATFile.getEconomicActivity()) {
                        case Consumption -> currentConsumptionValue += observation.getVolume();
                        case Production -> currentProductionValue += observation.getVolume();
                        default ->
                                throw new IllegalStateException("Unexpected value: " + currentSDATFile.getEconomicActivity());
                    }
                    // Store the absolute values in the cache
                    Date date = new Date(observation.getRelativeTime());
                    MeterReading[] meterReadings = meterReadingCache.getObservationHashMap().get(date);
                    if (meterReadings == null) {
                        meterReadings = new MeterReading[2];
                        meterReadings[0] = MeterReading.builder().type(EconomicActivity.Consumption).build();
                        meterReadings[1] = MeterReading.builder().type(EconomicActivity.Production).build();
                        meterReadingCache.getObservationHashMap().put(date, meterReadings);
                    }
                    meterReadings[0].setValue(currentConsumptionValue);
                    meterReadings[1].setValue(currentProductionValue);
                }
            }
            // Move to the next SDATFile
            sdatIndex++;
            if (sdatIndex < fileDateSdatFilesList.size()) {
                currentSDATFiles = fileDateSdatFilesList.get(sdatIndex).getSDATFiles();
            }
        }

        ConcurrentHashMap<Date, MeterReading[]> observationHashMap = meterReadingCache.getObservationHashMap();
        observationHashMap.entrySet().stream().sorted(Map.Entry.comparingByKey(Date::compareTo)).forEach(entry -> {
            // Debug prints
//             System.out.println(entry.getKey() + ":\n\t" + entry.getValue()[0].getType() + ": " + entry.getValue()[0].getValue() + "\n\t" + entry.getValue()[1].getType() + ": " + entry.getValue()[1].getValue());
        });
    }
}
