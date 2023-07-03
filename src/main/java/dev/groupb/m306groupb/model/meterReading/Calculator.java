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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Calculator {
    ESLCache eslCache;
    SDATCache sdatCache;
    private LocalDate date;
    private double value;
    private EconomicActivity economicActivity;

    public Calculator() {
        eslCache = ESLCache.getInstance();
        sdatCache = SDATCache.getInstance();
    }

    public static void calcuteValues(List<SDATFileWithDate> fileDateSdatFilesList, List<ESLFileWithDate> fileDateESLFilesList) {
        int indexESLFiles = 0;
        int indexSDATFiles = 0;
        double valueConsumption = 0;
        double valueProduction = 0;
        if (fileDateESLFilesList.get(0).getFileDate().getStartDate().before(fileDateSdatFilesList.get(0).getFileDate().getStartDate())) {
            MeterReadingCache meterReadingCache = MeterReadingCache.getInstance();
            for (; indexESLFiles < fileDateESLFilesList.size(); indexESLFiles++) {
                valueConsumption = fileDateESLFilesList.get(indexESLFiles).getESLFiles().getHighTariffConsumption() + fileDateESLFilesList.get(indexESLFiles).getESLFiles().getLowTariffConsumption();
                Calendar timeConsumption = Calendar.getInstance();
                Calendar timeProduction = Calendar.getInstance();
                timeConsumption.setTime(fileDateESLFilesList.get(indexESLFiles).getFileDate().getStartDate());
                valueProduction = fileDateESLFilesList.get(indexESLFiles).getESLFiles().getHighTariffProduction() + fileDateESLFilesList.get(indexESLFiles).getESLFiles().getLowTariffProduction();
                for (; indexSDATFiles < fileDateSdatFilesList.size(); indexSDATFiles++) {
                    if (fileDateSdatFilesList.get(indexSDATFiles).getFileDate().getStartDate().before(fileDateESLFilesList.get(indexESLFiles).getFileDate().getStartDate())) {
                        break;
                    }
                    for (SDATFile sdatFile : fileDateSdatFilesList.get(indexSDATFiles).getSDATFiles()) {
                        switch (sdatFile.getEconomicActivity()) {
                            case Production -> {
                                int temp = 0;
                                for (Observation observation : sdatFile.getObservations()) {
                                    if (temp != 0) {
                                        timeProduction.add(Calendar.MINUTE, 15);
                                    } else {
                                        temp++;
                                    }
                                    valueProduction += observation.getVolume();
                                    FileDate fileDate = FileDate.builder().startDate(timeProduction.getTime()).build();
                                    MeterReading meterReading = new MeterReading();
                                    meterReading.setValue(valueProduction);
                                    System.out.println("Production at " + fileDate.getStartDate() + ": " + valueConsumption);
                                    meterReading.setType(EconomicActivity.Production);
                                    meterReadingCache.getObservationHashMap().put(fileDate, meterReading);
                                }
                            }
                            case Consumption -> {
                                int temp = 0;
                                for (Observation observation : sdatFile.getObservations()) {
                                    if (temp != 0) {
                                        timeConsumption.add(Calendar.MINUTE, 15);
                                    } else {
                                        temp++;
                                    }
                                    valueConsumption += observation.getVolume();
                                    FileDate fileDate = FileDate.builder().startDate(timeConsumption.getTime()).build();
                                    MeterReading meterReading = new MeterReading();
                                    meterReading.setValue(valueConsumption);
                                    System.out.println("Consumption at " + fileDate.getStartDate() + ": " + valueConsumption);
                                    meterReading.setType(EconomicActivity.Consumption);
                                    meterReadingCache.getObservationHashMap().put(fileDate, meterReading);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Calendar timeConsumption = Calendar.getInstance();
            Calendar timeProduction = Calendar.getInstance();
            MeterReadingCache meterReadingCache = MeterReadingCache.getInstance();
            for (; indexSDATFiles < fileDateSdatFilesList.size(); indexSDATFiles++) {
                for (SDATFile sdatFile : fileDateSdatFilesList.get(indexSDATFiles).getSDATFiles()) {
                    switch (sdatFile.getEconomicActivity()) {
                        case Production -> {
                            int temp = 0;
                            for (Observation observation : sdatFile.getObservations()) {
                                if (temp != 0) {
                                    timeProduction.add(Calendar.MINUTE, 15);
                                } else {
                                    temp++;
                                }
                                valueProduction += observation.getVolume();
                                FileDate fileDate = FileDate.builder().startDate(timeProduction.getTime()).build();
                                MeterReading meterReading = new MeterReading();
                                meterReading.setValue(valueProduction);
                                System.out.println("Production at " + fileDate.getStartDate() + ": " + valueConsumption);
                                meterReading.setType(EconomicActivity.Production);
                                meterReadingCache.getObservationHashMap().put(fileDate, meterReading);
                            }
                        }
                        case Consumption -> {
                            int temp = 0;
                            for (Observation observation : sdatFile.getObservations()) {
                                if (temp != 0) {
                                    timeConsumption.add(Calendar.MINUTE, 15);
                                } else {
                                    temp++;
                                }
                                valueConsumption += observation.getVolume();
                                FileDate fileDate = FileDate.builder().startDate(timeConsumption.getTime()).build();
                                MeterReading meterReading = new MeterReading();
                                meterReading.setValue(valueConsumption);
                                System.out.println("Consumption at " + fileDate.getStartDate() + ": " + valueConsumption);
                                meterReading.setType(EconomicActivity.Consumption);
                                meterReadingCache.getObservationHashMap().put(fileDate, meterReading);
                            }
                        }
                    }
                }
                for (; indexESLFiles < fileDateESLFilesList.size(); indexESLFiles++) {
                    if (fileDateSdatFilesList.get(indexSDATFiles).getFileDate().getStartDate().after(fileDateESLFilesList.get(indexESLFiles).getFileDate().getStartDate())) {
                        break;
                    }
                    valueConsumption = fileDateESLFilesList.get(indexESLFiles).getESLFiles().getHighTariffConsumption() + fileDateESLFilesList.get(indexESLFiles).getESLFiles().getLowTariffConsumption();
                    timeConsumption.setTime(fileDateESLFilesList.get(indexESLFiles).getFileDate().getStartDate());
//                    valueProduction = fileDateESLFilesList.get(indexESLFiles).getESLFiles().getHighTariffProduction() + fileDateESLFilesList.get(indexESLFiles).getESLFiles().getLowTariffProduction();
                }
            }
        }
    }

    public void sortValues() {
        ConcurrentHashMap<FileDate, ESLFile> eslFileHashMap = eslCache.getEslFileMap();
        // sort by start date
        List<ESLFileWithDate> fileDateESLFilesList = new ArrayList<>(eslFileHashMap.entrySet().stream()
                .map(fileDateEntry -> ESLFileWithDate
                        .builder()
                        .fileDate(fileDateEntry.getKey())
                        .ESLFiles(fileDateEntry.getValue())
                        .build())
                .toList());
        fileDateESLFilesList.sort(ESLFileWithDate::compareTo);

        ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = sdatCache.getSdatFileHashMap();
        // sort by start date
        List<SDATFileWithDate> fileDateSdatFilesList = new java.util.ArrayList<>(sdatFileHashMap.entrySet().stream().parallel()
                .map(entry -> SDATFileWithDate.builder().fileDate(entry.getKey()).SDATFiles(entry.getValue()).build())
                .toList());
        fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

        calcuteValues(fileDateSdatFilesList, fileDateESLFilesList);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public EconomicActivity getEconomicActivity() {
        return economicActivity;
    }

    public void setEconomicActivity(EconomicActivity economicActivity) {
        this.economicActivity = economicActivity;
    }
}
