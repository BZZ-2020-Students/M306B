package dev.groupb.m306groupb.controller;

import static org.junit.jupiter.api.Assertions.*;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.SDATFile.SDATCache;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
class CsvExportServiceTest {

    @Mock
    private SDATCache cacheData;

    @Mock
    private HttpServletResponse response;

    private CsvExportService csvExportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        csvExportService = new CsvExportService();
    }

    @Test
    void testExportDataInRange() throws IOException {
        // Mock the necessary objects and dependencies
        Date from = new Date();
        Date to = new Date();
        SDATFile[] files = {mock(SDATFile.class)};
        SDATCache mockCacheData = mock(SDATCache.class);
        when(cacheData.getSdatFileHashMap()).thenReturn(new HashMap<>());
        when(cacheData.getSdatFileHashMap().entrySet()).thenReturn(new HashSet<>());
        when(cacheData.getSdatFileHashMap().entrySet().iterator()).thenReturn(Collections.emptyIterator());
        when(mockCacheData.getSdatFileHashMap().entrySet().iterator().hasNext()).thenReturn(true, false);
        when(mockCacheData.getSdatFileHashMap().entrySet().iterator().next()).thenReturn(mock(Map.Entry.class));
        when(mockCacheData.getSdatFileHashMap().entrySet().iterator().next().getKey()).thenReturn(mock(FileDate.class));
        when(mockCacheData.getSdatFileHashMap().entrySet().iterator().next().getValue()).thenReturn(files);
        when(csvExportService.isWithinTimeRange(any(), any(), any())).thenReturn(true);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        when(response.getWriter().append(any(CharSequence.class))).thenReturn(mock(PrintWriter.class));

        // Perform the test
        ResponseEntity<?> responseEntity = csvExportService.exportDataInRange(from, to, response);

        // Verify the interactions and assertions
        verify(cacheData, times(3)).getSdatFileHashMap();
        verify(csvExportService, times(1)).isWithinTimeRange(any(), any(), any());
        verify(response, times(1)).setHeader(eq("Content-Disposition"), eq("attachment; filename=data.csv"));
        verify(response.getWriter(), times(1)).write(anyString());
        verify(response.getWriter(), times(1)).flush();
        assertEquals(ResponseEntity.ok().build(), responseEntity);
    }
}
