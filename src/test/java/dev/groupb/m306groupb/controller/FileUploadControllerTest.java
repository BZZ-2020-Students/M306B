package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.storage.StorageFileNotFoundException;
import dev.groupb.m306groupb.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FileUploadControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
    public void shouldSaveUploadedFileSDAT() throws Exception {
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        MockMultipartFile multipartFile = new MockMultipartFile("file", "newSDAT_" + currentTimestamp + ".xml",
                "application/xml", "Spring Framework".getBytes());
        this.mvc.perform(multipart("/sdat").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.storageService).should().store(multipartFile, FileType.SDAT);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource("test.txt", FileType.SDAT))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/files/sdat/test.txt")).andExpect(status().isNotFound());
    }
}
