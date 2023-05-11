package dev.groupb.m306groupb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.model.SDATFile.SDATFileWithDate;
import dev.groupb.m306groupb.utils.GlobalStuff;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Controller
public class SdatOfDayController {
    @GetMapping("/sdat-day")
    public String greeting(
            @RequestParam(name = "startDate", required = false, defaultValue = "NULL") String startDate,
            @RequestParam(name = "endDate", required = false, defaultValue = "NULL") String endDate,
            @RequestParam(name = "creationDate", required = false, defaultValue = "NULL") String creationDate,
            Model model
    ) {
        if (startDate.equals("NULL")) {
            startDate = null;
        }
        if (endDate.equals("NULL")) {
            endDate = null;
        }
        if (creationDate.equals("NULL")) {
            creationDate = null;
        }
        if (startDate == null && endDate == null && creationDate == null) {
            return "sdat_of_day"; // TODO: Show error message
        }

        SDATCache sdatCache = SDATCache.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.XML_DATE_FORMAT);

        try {
            FileDate fileDate = FileDate.builder()
                    .startDate(startDate != null ? simpleDateFormat.parse(startDate) : null)
                    .endDate(endDate != null ? simpleDateFormat.parse(endDate) : null)
                    .fileCreationDate(creationDate != null ? simpleDateFormat.parse(creationDate) : null)
                    .build();

            Map.Entry<FileDate, SDATFile[]> foundEntry = sdatCache.getSdatFileHashMap().entrySet().stream()
                    .filter(entry -> fileDate.getStartDate() == null || entry.getKey().getStartDate().equals(fileDate.getStartDate()))
                    .filter(entry -> fileDate.getEndDate() == null || entry.getKey().getEndDate().equals(fileDate.getEndDate()))
                    .filter(entry -> fileDate.getFileCreationDate() == null || entry.getKey().getFileCreationDate().equals(fileDate.getFileCreationDate()))
                    .findFirst()
                    .orElse(null);

            // Serialize to json
            if (foundEntry != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setDateFormat(simpleDateFormat);
                model.addAttribute("sdatFiles", objectMapper.writeValueAsString(SDATFileWithDate.builder().SDATFiles(foundEntry.getValue()).fileDate(foundEntry.getKey()).build()));
            }
        } catch (ParseException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return "sdat_of_day";
    }
}
