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
    @GetMapping("/sdat-view")
    public String sdatView(
            @RequestParam(name = "dateRange") String dateRange,
            Model model
    ) {
        SDATCache sdatCache = SDATCache.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.XML_DATE_FORMAT);

        try {
            FileDate fileDate = FileDate.builder()
                    .startDate(simpleDateFormat.parse(dateRange))
                    .build();

            Map.Entry<FileDate, SDATFile[]>[] foundEntries = sdatCache.getSdatFileHashMap().entrySet().stream()
                    .filter(entry -> fileDate.getStartDate() == null || entry.getKey().getStartDate().equals(fileDate.getStartDate()))
                    .toArray(Map.Entry[]::new);

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
