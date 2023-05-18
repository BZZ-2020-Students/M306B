package dev.groupb.m306groupb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/api/upload")
@RestController
public class uploadDataService {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @PostMapping("/files")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Determine the appropriate directory based on the file type
            String fileType = determineFileType(file);
            String directory = determineDirectory(fileType);

            // Save the file to the directory
            File savedFile = new File(directory, file.getOriginalFilename());
            file.transferTo(savedFile);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
    private String determineFileType(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // Read the first few bytes of the file content
            byte[] bytes = new byte[8];
            inputStream.read(bytes);

            // Check the file signature
            if (isESLFile(bytes)) {
                return "ESL";
            } else if (isSDATFile(bytes)) {
                return "SDAT";
            } else {
                throw new IllegalArgumentException("Invalid file type");
            }
        }
    }

    public String determineDirectory(String fileType) throws Exception {
        if(fileType.equals("ESL")){
            return esl_files_path;
        }
        else if(fileType.equals("SDAT")){
            return sdat_files_path;
        }
        else {
            throw new Exception("Filetype is neither ESL or SDAT!");
        }
    }

    private boolean isESLFile(byte[] bytes) {
        // Check the file signature or magic number for ESL files
        // Example: Check if the first few bytes match the expected signature for ESL files
        return bytes[0] == 0x45 && bytes[1] == 0x53 && bytes[2] == 0x4C;
    }

    private boolean isSDATFile(byte[] bytes) {
        // Check the file signature or magic number for SDAT files
        // Example: Check if the first few bytes match the expected signature for SDAT files
        return bytes[0] == 0x53 && bytes[1] == 0x44 && bytes[2] == 0x41 && bytes[3] == 0x54;
    }
}
