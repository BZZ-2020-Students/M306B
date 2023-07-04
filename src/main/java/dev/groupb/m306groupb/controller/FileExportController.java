package dev.groupb.m306groupb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.groupb.m306groupb.enums.DiagramTypes;
import dev.groupb.m306groupb.enums.ExportTypes;
import dev.groupb.m306groupb.enums.Unit;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.Observation;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.utils.GlobalStuff;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/export")
public class FileExportController {
    private final SDATCache cacheData = SDATCache.getInstance();

    /**
     * Exports the data to a specified format
     *
     * @param exportType The type of the export (csv, json)
     * @param from     The start date of the export
     * @param to       The end date of the export
     * @param response The response object
     * @throws IOException If the export fails
     */
    @GetMapping(
            value = "/{exportType}/{dataType}/{from}/{to}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void exportDataCSV(
            @PathVariable("exportType") String exportType,
            @PathVariable("dataType") String dataType,
            @PathVariable("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @PathVariable("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            HttpServletResponse response
    ) throws IOException {
        DiagramTypes diagramType = DiagramTypes.fromString(dataType);

        // Retrieve the relevant SDATFiles from the SDATCache within the specified time range
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(from);
        cal2.setTime(to);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        Map<FileDate, SDATFile[]> filteredMap;
        if (sameDay) {
            filteredMap = cacheData.getSdatFileHashMap().entrySet().stream().parallel()
                    .filter(entry -> {
                        Calendar cal3 = Calendar.getInstance();
                        cal3.setTime(entry.getKey().getStartDate());
                        return cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) &&
                                cal1.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            Calendar cal4 = Calendar.getInstance();
            cal4.setTime(to);
            cal4.add(Calendar.DATE, 1); // add one day to make the range inclusive
            Date inclusiveLatestDate = cal4.getTime();
            filteredMap = cacheData.getSdatFileHashMap().entrySet().stream().parallel()
                    .filter(entry ->
                            !entry.getKey().getStartDate().before(from) && !entry.getKey().getStartDate().after(inclusiveLatestDate)
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Sort the map by using the CompareTo method of the FileDate class
        Map<FileDate, SDATFile[]> sortedMap = new TreeMap<>(filteredMap);

        switch (ExportTypes.fromString(exportType)) {
            case CSV -> exportDataCSV(response, sortedMap, from, to);
            case JSON -> exportDataJSON(response, sortedMap, from, to);
        }
    }

    private void exportDataCSV(HttpServletResponse response, Map<FileDate, SDATFile[]> filteredMap, Date from, Date to) throws IOException {
        response.setContentType("text/csv");
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.FILENAME_DATE_FORMAT);
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";

        String fromDate = dateFormat.format(from);
        String toDate = dateFormat.format(to);
        String fileName = "data_" + currentDateTime + "_from_" + fromDate + "_to_" + toDate + ".csv";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        // Generate CSV content
        String[] csvHeader = {"timestamp", "consumption", "production"};

        try (ICsvListWriter listWriter = new CsvListWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE)) {
            listWriter.writeHeader(csvHeader);

            for (Map.Entry<FileDate, SDATFile[]> entry : filteredMap.entrySet()) {
                FileDate fileDate = entry.getKey();
                SDATFile[] sdatFiles = entry.getValue();

                SDATFile consumptionFile = null;
                SDATFile productionFile = null;

                for (SDATFile sdatFile : sdatFiles) {
                    switch (sdatFile.getEconomicActivity()) {
                        case Production -> productionFile = sdatFile;
                        case Consumption -> consumptionFile = sdatFile;
                    }
                }

                if (consumptionFile == null || productionFile == null) {
                    System.err.println("Could not find both consumption and production file for date " + fileDate.getStartDate() + "!");
                    continue;
                }

                SortedSet<Observation> consumptionObservations = consumptionFile.getObservations();
                SortedSet<Observation> productionObservations = productionFile.getObservations();

                Iterator<Observation> consumptionIterator = consumptionObservations.iterator();
                Iterator<Observation> productionIterator = productionObservations.iterator();

                while (consumptionIterator.hasNext() && productionIterator.hasNext()) {
                    Observation consumptionObservation = consumptionIterator.next();
                    Observation productionObservation = productionIterator.next();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(fileDate.getStartDate());
                    if (Objects.requireNonNull(consumptionFile.getResolution().getTimeUnit()) == Unit.MIN) {
                        calendar.add(Calendar.MINUTE, consumptionFile.getResolution().getResolution() * (consumptionObservation.getPosition() - 1));
                    } else {
                        throw new IllegalStateException("Unexpected value: " + consumptionFile.getResolution().getTimeUnit());
                    }

                    long timestamp = calendar.getTimeInMillis();
                    double consumptionVolume = consumptionObservation.getVolume();
                    double productionVolume = productionObservation.getVolume();

                    listWriter.write(timestamp, consumptionVolume, productionVolume);
                }
            }
        }
    }

    private void exportDataJSON(HttpServletResponse response, Map<FileDate, SDATFile[]> filteredMap, Date from, Date to) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.SDAT_DATE_FORMAT);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(simpleDateFormat);
        String json = objectMapper.writeValueAsString(filteredMap);

        response.setContentType("application/json");
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.FILENAME_DATE_FORMAT);
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";

        String fromDate = dateFormat.format(from);
        String toDate = dateFormat.format(to);
        String fileName = "data_" + currentDateTime + "_from_" + fromDate + "_to_" + toDate + ".json";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        response.getWriter().write(json);
    }
}
