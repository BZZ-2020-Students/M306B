package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/export")
public class CsvExportService {
    private final SDATCache cacheData = SDATCache.getInstance();

    @Value("${exported.files.path}")
    private String exported_files_path;

    /**
     * Exports data as a CSV file within a specific time range.
     *
     * @param from     Start date of the time range (in yyyy-MM-dd format)
     * @param to       End date of the time range (in yyyy-MM-dd format)
     * @param response HttpServletResponse to set the CSV file as the response
     * @return ResponseEntity with the CSV file as the response body
     */
    @GetMapping(
            value = "/range/{from}/{to}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<?> exportDataInRange(
            @PathVariable("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @PathVariable("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            HttpServletResponse response
    ) throws IOException {
        // Retrieve the relevant SDATFiles from the SDATCache within the specified time range
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(from);
        cal2.setTime(to);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        Map<FileDate, SDATFile[]> filteredMap = new HashMap<>();
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

        System.out.println("FilteredMap: " + filteredMap);

        // Generate CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Start,End,EconomicActivity,Resolution,MeasureUnit,Observations\n");
        for (FileDate dates : filteredMap.keySet()) {
            for (SDATFile file : filteredMap.get(dates)) {
                csvContent.append(dates.getStartDate()).append(","); // Append Start
                csvContent.append(dates.getEndDate()).append(","); // Append EndEconomicActivity
                csvContent.append(file.getEconomicActivity()).append(","); // Append EconomicActivity
                csvContent.append(file.getResolution()).append(","); // Append Resolution
                csvContent.append(file.getMeasureUnit()).append(","); // Append MeasureUnit
                csvContent.append(file.getObservations()).append("\n"); // Append Observations
            }
        }

        // Generate the dynamic file name with the specified date range
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String fromDate = dateFormat.format(from);
        String toDate = dateFormat.format(to);
        String fileName = "data_" + fromDate + "_to_" + toDate + ".csv";

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok().headers(headers).build();
    }

    private boolean isWithinTimeRange(FileDate fileDate, Date start, Date end) {
        return !fileDate.getStartDate().after(end) && !fileDate.getStartDate().before(start);
    }

    private void writeCsvToFile(String csvContent, String fileName) throws IOException {
        String filePath = exported_files_path + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(csvContent);
        }
    }
}
