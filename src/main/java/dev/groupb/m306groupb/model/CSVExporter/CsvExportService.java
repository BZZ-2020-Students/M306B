package dev.groupb.m306groupb.model.CSVExporter;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
            @PathParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @PathParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
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

    private boolean isWithinTimeRange(FileDate fileDate, Date start, Date end) {
        return !fileDate.getStartDate().after(end) && !fileDate.getStartDate().before(start);
    }
}
