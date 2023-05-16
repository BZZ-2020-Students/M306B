package dev.groupb.m306groupb.model.CSVExporter;

import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/export")
public class CsvExportService {
    SDATCache cacheData = SDATCache.getInstance();

    /**
     * TODO: As a user, I would like to have a REST service to export data as a CSV file in a specific area.
     * TODO export as CSV File
    */





    //TODO Full Year
    /**
     *
     * @param response
     * @return
     */
    @GetMapping("/full-year")
    public ResponseEntity<?> exportFullYear(HttpServletResponse response) {
        //cacheData.getSdatFileHashMap()
        return null;
    }

    //TODO Full Month
    /**
     *
     * @param response
     * @return
     */
    @GetMapping("/full-month")
    public ResponseEntity<?> exportFullMonth(HttpServletResponse response) {
        return null;
    }

    //TODO Full Week
    /**
     *
     * @param response
     * @return
     */
    @GetMapping("/full-week")
    public ResponseEntity<?> exportFullWeek(HttpServletResponse response) {
        return null;
    }

    //TODO Full Day
    /**
     *
     * @param response
     * @return
     */
    @GetMapping("/full-day")
    public ResponseEntity<?> exportFullDay(HttpServletResponse response) {
        return null;
    }

    //TODO Range between day x and day
    /**
     *
     * @param start
     * @param end
     * @param response
     * @return
     */
    @GetMapping("/range/{start}/{end}")
    public ResponseEntity<?> exportDataInRange(@PathVariable("start") String start, @PathVariable("end") String end, HttpServletResponse response) {
        LocalDate startDate = LocalDate.parse(start); // Parse start date from the path variable
        LocalDate endDate = LocalDate.parse(end); // Parse end date from the path variable
        //List<Data> data = dataService.getDataInRange(startDate, endDate); // Replace with your logic to retrieve data
        return null;
    }

    /**
     *
     * @param response
     * @return
     */

    private ResponseEntity<?> exportToCsv(HttpServletResponse response) {
        // Logic to export data as CSV file
        // Set the response headers and write the CSV content
        // You can use libraries like OpenCSV or Apache Commons CSV to simplify CSV generation

        return ResponseEntity.ok().build();
    }


}
