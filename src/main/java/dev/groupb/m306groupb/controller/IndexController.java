package dev.groupb.m306groupb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.groupb.m306groupb.enums.DiagramTypes;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class IndexController {
    @GetMapping("/")
    public String load_index(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
                             @RequestParam(required = false) String type,
                             Model model) {
        DiagramTypes diagramType = DiagramTypes.fromString(type);
        model.addAttribute("chartType", diagramType.toString());

        switch (diagramType) {
            case USAGE -> {
                SDATCache sdatCache = SDATCache.getInstance();

                try {
                    ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = sdatCache.getSdatFileHashMap();
                    // sort by start date
                    List<SDATFileWithDate> fileDateSdatFilesList = new java.util.ArrayList<>(sdatFileHashMap.entrySet().stream()
                            .map(entry -> SDATFileWithDate.builder().fileDate(entry.getKey()).SDATFiles(entry.getValue()).build())
                            .toList());
                    fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

                    // get the earliest date and the latest date if they aren't specified in the request
                    Date earliestDate = (from == null) ? fileDateSdatFilesList.get(0).getFileDate().getStartDate() : from;
                    Date latestDate = (to == null) ? fileDateSdatFilesList.get(fileDateSdatFilesList.size() - 1).getFileDate().getStartDate() : to;

                    // filter the list
                    fileDateSdatFilesList = new java.util.ArrayList<>(fileDateSdatFilesList.stream()
                            .filter(sdatFileWithDate -> !sdatFileWithDate.getFileDate().getStartDate().before(earliestDate) && !sdatFileWithDate.getFileDate().getStartDate().after(latestDate))
                            .toList());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.SDAT_DATE_FORMAT);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(simpleDateFormat);
                    String json = objectMapper.writeValueAsString(fileDateSdatFilesList);

                    model.addAttribute("sdatFiles", json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case METER -> {
//                throw new UnsupportedOperationException("Not implemented yet");
            }
            default -> throw new IllegalStateException("Unexpected value: " + diagramType);
        }

        return "index";
    }
}
