package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.utils.FileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SdatOfDayController {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @GetMapping("/sdat-day")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String date, Model model) {
        System.out.println(sdat_files_path);
        FileReader.readFile(sdat_files_path + "20190313_093127_12X-0000001216-O_E66_12X-LIPPUNEREM-T_ESLEVU121963_-279617263.xml");

        return "sdat_of_day";
    }
}
