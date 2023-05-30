package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;
import dev.groupb.m306groupb.model.ESLFile.ESLCache;
import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.ESLFile.ESLFileWithDate;
import dev.groupb.m306groupb.model.FileDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Calculator {
    private LocalDate date;
    private double value;
    private EconomicActivity economicActivity;

    ESLCache eslCache;

    public Calculator() {
        eslCache = ESLCache.getInstance();
    }

    public void sortValues() {
        HashMap<FileDate, ESLFile[]> eslFileHashMap = (HashMap<FileDate, ESLFile[]>) eslCache.getEslFileMap();
        // sort by start date
        List<ESLFileWithDate> fileDateESLFilesList = new ArrayList<>(eslFileHashMap.entrySet().stream()
                .map(fileDateEntry -> ESLFileWithDate
                    .builder()
                    .fileDate(fileDateEntry.getKey())
                    .ESLFiles(fileDateEntry.getValue())
                    .build())
                    .toList());
        fileDateESLFilesList.sort(ESLFileWithDate::compareTo);
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
