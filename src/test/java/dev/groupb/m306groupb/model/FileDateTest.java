package dev.groupb.m306groupb.model;

import dev.groupb.m306groupb.utils.GlobalStuff;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FileDateTest {
    @Test
    void testEquals() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalStuff.SDAT_DATE_FORMAT);

        FileDate fileDate = FileDate.builder().fileCreationDate(null).startDate(null).endDate(null).build();
        FileDate fileDate2 = FileDate.builder().fileCreationDate(null).startDate(null).endDate(null).build();
        assertEquals(fileDate, fileDate2);

        fileDate = FileDate.builder().fileCreationDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).startDate(null).endDate(null).build();
        fileDate2 = FileDate.builder().fileCreationDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).startDate(null).endDate(null).build();
        assertEquals(fileDate, fileDate2);

        fileDate = FileDate.builder().fileCreationDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).startDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).endDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).build();
        fileDate2 = FileDate.builder().fileCreationDate(simpleDateFormat.parse("2019-03-11T23:00:00Z")).startDate(null).endDate(null).build();
        assertNotEquals(fileDate, fileDate2);
    }
}
