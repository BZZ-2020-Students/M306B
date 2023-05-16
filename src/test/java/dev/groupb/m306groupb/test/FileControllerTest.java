package dev.groupb.m306groupb.test;
import dev.groupb.m306groupb.service.uploadDataService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

@WebMvcTest(uploadDataService.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private uploadDataService fileController;

//    @Mock
//    private FileService fileService;

    @Test
    public void testUploadFile() throws Exception {
        byte[] eslContent = getEslFileContent(); // Replace with your ESL file content as byte array

        MockMultipartFile file = new MockMultipartFile("file", "test.esl", "application/octet-stream", eslContent);

        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/files")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File uploaded successfully"));
    }

    private byte[] getEslFileContent() {
        // Replace with your logic to obtain the content of the ESL file as byte array
        String eslContentString = "ESL file content";
        return eslContentString.getBytes(StandardCharsets.UTF_8);
    }
}
