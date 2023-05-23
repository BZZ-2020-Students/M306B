package dev.groupb.m306groupb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.model.SDATFile.SDATFileWithDate;
import dev.groupb.m306groupb.utils.GlobalStuff;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SdatOfDayController {
    @GetMapping("/sdat-view")
    public String sdatView(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            Model model
    ) {
        SDATCache sdatCache = SDATCache.getInstance();

        try {
            HashMap<FileDate, SDATFile[]> sdatFileHashMap = sdatCache.getSdatFileHashMap();
            SDATFile[] sdatFiles = sdatFileHashMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().getStartDate().before(from) && !entry.getKey().getStartDate().after(to))
                    .flatMap(entry -> Arrays.stream(entry.getValue()))
                    .toArray(SDATFile[]::new);



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
