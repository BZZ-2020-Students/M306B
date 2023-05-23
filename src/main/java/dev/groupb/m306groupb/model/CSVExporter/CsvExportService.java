package dev.groupb.m306groupb.model.CSVExporter;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
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
     * @param start    Start date of the time range (in yyyy-MM-dd format)
     * @param end      End date of the time range (in yyyy-MM-dd format)
     * @param response HttpServletResponse to set the CSV file as the response
     * @return ResponseEntity with the CSV file as the response body
     */
    @GetMapping("/range/{start}/{end}")
    public ResponseEntity<?> exportDataInRange(
            @PathVariable("start") String start,
            @PathVariable("end") String end,
            HttpServletResponse response
    ) throws IOException {
        LocalDate startDate = LocalDate.parse(start); // Parse start date from the path variable
        LocalDate endDate = LocalDate.parse(end); // Parse end date from the path variable

        // Retrieve the relevant SDATFiles from the SDATCache within the specified time range
        Map<FileDate, SDATFile[]> filesInRange = new HashMap<>();
        for (Map.Entry<FileDate, SDATFile[]> entry : cacheData.getSdatFileHashMap().entrySet()) {
            FileDate fileDate = entry.getKey();
            if (isWithinTimeRange(fileDate, startDate, endDate)) {
                filesInRange.put(fileDate, entry.getValue());
            }
        }

        // Generate CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Header 1,Header 2,Header 3\n");
        for (SDATFile[] files : filesInRange.values()) {
            for (SDATFile file : files) {
                csvContent.append(file.getFileName()).append(",");
                csvContent.append(file.getFileType()).append(",");
                csvContent.append(file.getResolution()).append("\n");
            }
        }

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.csv");

        // Write the CSV content to the response output stream
        response.setHeader("Content-Disposition", "attachment; filename=data.csv");
        response.setContentType("text/csv");
        response.getWriter().write(csvContent.toString());
        response.getWriter().flush();

        return ResponseEntity.ok().build();
    }

    private boolean isWithinTimeRange(FileDate fileDate, LocalDate start, LocalDate end) {
        Date startDate = fileDate.getStartDate();
        Date endDate = fileDate.getEndDate();

        // Convert the java.util.Date to LocalDate for comparison
        LocalDate convertedStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate convertedEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return !convertedStartDate.isAfter(end) && !convertedEndDate.isBefore(start);
    }
}
