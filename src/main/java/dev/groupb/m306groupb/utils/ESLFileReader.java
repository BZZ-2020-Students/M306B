package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.FileDate;

import java.io.File;
import java.text.SimpleDateFormat;

public class ESLFileReader implements FileReader<ESLFile>{
    SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.XML_DATE_FORMAT);

    @Override
    public FileDate getFileDate(File file) {
        return null;
    }

    @Override
    public ESLFile parseFile(File file) {
        return null;
            /*ESLFile.builder()
                .highTariffConsumption()
                .build();*/
    }
}
