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
import java.util.Calendar;
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
        model.addAttribute("chartType", DiagramTypes.toJson(diagramType));

        switch (diagramType) {
            case USAGE -> {
                SDATCache sdatCache = SDATCache.getInstance();

                try {
                    ConcurrentHashMap<FileDate, SDATFile[]> sdatFileHashMap = sdatCache.getSdatFileHashMap();
// sort by start date
                    List<SDATFileWithDate> fileDateSdatFilesList = new java.util.ArrayList<>(sdatFileHashMap.entrySet().stream().parallel()
                            .map(entry -> SDATFileWithDate.builder().fileDate(entry.getKey()).SDATFiles(entry.getValue()).build())
                            .toList());
                    fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

// get the earliest date and the latest date if they aren't specified in the request
                    Date earliestDate = (from == null) ? fileDateSdatFilesList.get(0).getFileDate().getStartDate() : from;
                    Date latestDate = (to == null) ? fileDateSdatFilesList.get(fileDateSdatFilesList.size() - 1).getFileDate().getStartDate() : to;

                    SimpleDateFormat frontendDayFormat = new SimpleDateFormat(GlobalStuff.ONLY_DAY_FORMAT);
                    model.addAttribute("earliestDate", frontendDayFormat.format(earliestDate));
                    model.addAttribute("latestDate", frontendDayFormat.format(latestDate));

                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(earliestDate);
                    cal2.setTime(latestDate);
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

                    if (sameDay) {
                        // if from and to dates are the same day, return data for that day
                        fileDateSdatFilesList = new java.util.ArrayList<>(fileDateSdatFilesList.stream().parallel()
                                .filter(sdatFileWithDate -> {
                                    Calendar cal3 = Calendar.getInstance();
                                    cal3.setTime(sdatFileWithDate.getFileDate().getStartDate());
                                    return cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) &&
                                            cal1.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR);
                                })
                                .toList());
                    } else {
                        // filter the list
                        Calendar cal4 = Calendar.getInstance();
                        cal4.setTime(latestDate);
                        cal4.add(Calendar.DATE, 1); // add one day to make the range inclusive
                        Date inclusiveLatestDate = cal4.getTime();
                        fileDateSdatFilesList = new java.util.ArrayList<>(fileDateSdatFilesList.stream().parallel()
                                .filter(sdatFileWithDate -> !sdatFileWithDate.getFileDate().getStartDate().before(earliestDate) && !sdatFileWithDate.getFileDate().getStartDate().after(inclusiveLatestDate))
                                .toList());
                    }

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
