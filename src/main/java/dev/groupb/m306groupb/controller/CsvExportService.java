package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/export")
public class CsvExportService {
    private final SDATCache cacheData = SDATCache.getInstance();

    /**
     * Exports data as a CSV file within a specific time range.
     *
     * @param from     Start date of the time range (in yyyy-MM-dd format)
     * @param to       End date of the time range (in yyyy-MM-dd format)
     * @param response HttpServletResponse to set the CSV file as the response
     * @return ResponseEntity with the CSV file as the response body
     */
    @GetMapping("/range/{from}/{to}")
    public ResponseEntity<?> exportDataInRange(
            @PathVariable("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @PathVariable("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            HttpServletResponse response
    ) throws IOException {
        // Retrieve the relevant SDATFiles from the SDATCache within the specified time range
        Map<FileDate, SDATFile[]> filesInRange = new HashMap<>();
        for (Map.Entry<FileDate, SDATFile[]> entry : cacheData.getSdatFileHashMap().entrySet()) {
            FileDate fileDate = entry.getKey();
            if (isWithinTimeRange(fileDate, from, to)) {
                filesInRange.put(fileDate, entry.getValue());
            }
        }

        // Generate CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("EconomicActivity,Resolution,MeasureUnit,Observations\n");
        for (SDATFile[] files : filesInRange.values()) {
            for (SDATFile file : files) {
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

        // Save the CSV content to a dynamically generated file in the "Csv-File" directory
        writeCsvToFile(csvContent.toString(), fileName);

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok().headers(headers).build();
    }

    private boolean isWithinTimeRange(FileDate fileDate, Date start, Date end) {
        return !fileDate.getStartDate().after(end) && !fileDate.getStartDate().before(start);
    }

    private void writeCsvToFile(String csvContent, String fileName) throws IOException {
        // Get the current project folder
        Path currentPath = Paths.get("").toAbsolutePath();
        String projectFolder = currentPath.toString();

        // Create the "file" directory if it doesn't exist
        String fileDirectory = Paths.get(projectFolder, "files").toString();
        Path fileDirectoryPath = Paths.get(fileDirectory);
        if (!Files.exists(fileDirectoryPath)) {
            Files.createDirectories(fileDirectoryPath);
        }

        // Create the "Csv-File" subdirectory under the "file" directory if it doesn't exist
        String csvFileDirectory = Paths.get(fileDirectory, "Csv-File").toString();
        Path csvFileDirectoryPath = Paths.get(csvFileDirectory);
        if (!Files.exists(csvFileDirectoryPath)) {
            Files.createDirectories(csvFileDirectoryPath);
        }

        // Construct the file path in the "Csv-File" directory of the current project folder
        String filePath = Paths.get(csvFileDirectory, fileName).toString();

        // Check if the file already exists
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            throw new IOException("File already exists");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(csvContent);
        }
    }
}
