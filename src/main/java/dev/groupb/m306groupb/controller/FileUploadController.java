package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.storage.StorageFileNotFoundException;
import dev.groupb.m306groupb.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files/uploadForm")
    public String uploadForm() {
        return "uploadForm";
    }

    @PostMapping("files/upload/{type}")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String type,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file, FileType.fromString(type));
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/files/uploadForm";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
