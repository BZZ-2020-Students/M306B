package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SdatOfDayController {
    @GetMapping("/sdat-day")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String date, Model model) {
        SDATCache sdatCache = SDATCache.getInstance();
        System.out.println(sdatCache.getSdatFileHashMap().size());

        return "sdat_of_day";
    }
}
