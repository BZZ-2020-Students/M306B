package dev.groupb.m306groupb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.groupb.m306groupb.enums.DiagramTypes;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import dev.groupb.m306groupb.model.SDATFile.SDATFileWithDate;
import dev.groupb.m306groupb.model.meterReading.MeterReading;
import dev.groupb.m306groupb.model.meterReading.MeterReadingCache;
import dev.groupb.m306groupb.utils.GlobalStuff;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Controller
public class IndexController {
    @GetMapping("/")
    public String load_index(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
                             @RequestParam(required = false) String type,
                             Model model) {
        DiagramTypes diagramType = DiagramTypes.fromString(type);
        model.addAttribute("chartType", DiagramTypes.toJson(diagramType));
        SimpleDateFormat frontendDayFormat = new SimpleDateFormat(GlobalStuff.ONLY_DAY_FORMAT);

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
                    fileDateSdatFilesList.sort(SDATFileWithDate::compareTo);

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
                MeterReadingCache meterReadingCache = MeterReadingCache.getInstance();
                AtomicReference<Date> earliestDate = (from == null) ? new AtomicReference<>() : new AtomicReference<>(from);
                AtomicReference<Date> latestDate = (to == null) ? new AtomicReference<>() : new AtomicReference<>(to);

                try {
                    ConcurrentHashMap<Date, MeterReading[]> observationHashMap = meterReadingCache.getObservationHashMap();

                    AtomicInteger index = new AtomicInteger();
                    observationHashMap.entrySet().stream().parallel().sorted(Map.Entry.comparingByKey(Date::compareTo)).forEachOrdered(entry -> {
                        if (index.get() == 0 && earliestDate.get() == null) {
                            earliestDate.set(entry.getKey());
                            model.addAttribute("earliestDate", frontendDayFormat.format(earliestDate.get()));
                        } else if (index.get() == observationHashMap.size() - 1 && latestDate.get() == null) {
                            latestDate.set(entry.getKey());
                            model.addAttribute("latestDate", frontendDayFormat.format(latestDate.get()));
                        }

                        index.getAndIncrement();
                    });

                    Map<Long, MeterReading[]> filteredMap = observationHashMap.entrySet()
                            .stream()
                            .filter(entry -> {
                                Date entryDate = entry.getKey();
                                return (entryDate.after(earliestDate.get()) || isSameDay(entryDate, earliestDate.get()))
                                        && (entryDate.before(latestDate.get()) || isSameDay(entryDate, latestDate.get()));
                            })
                            .collect(Collectors.toMap(entry -> entry.getKey().getTime(), Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.SDAT_DATE_FORMAT);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(simpleDateFormat);
                    String json = objectMapper.writeValueAsString(filteredMap);
                    model.addAttribute("meterFiles", json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + diagramType);
        }

        return "index";
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
