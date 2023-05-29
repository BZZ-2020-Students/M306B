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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            Map<FileDate, SDATFile[]> filteredMap = sdatFileHashMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().getStartDate().before(from) && !entry.getKey().getStartDate().after(to))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<SDATFileWithDate> fileDateSdatFilesList = new java.util.ArrayList<>(filteredMap.entrySet().stream()
                    .map(entry -> SDATFileWithDate.builder().fileDate(entry.getKey()).SDATFiles(entry.getValue()).build())
                    .toList());

            // sort by start date
            fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.SDAT_DATE_FORMAT);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(simpleDateFormat);
            String json = objectMapper.writeValueAsString(fileDateSdatFilesList);

            model.addAttribute("sdatFiles", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return "sdat_of_day";
    }
}
