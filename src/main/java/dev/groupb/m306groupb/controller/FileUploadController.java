package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.storage.StorageFileNotFoundException;
import dev.groupb.m306groupb.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
public class FileUploadController {
    private final StorageService storageService;
    @Value("${sdat.files.path}")
    private String sdat_files_path;
    @Value("${esl.files.path}")
    private String esl_files_path;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files/list")
    public String listUploadedFiles(Model model) {
        model.addAttribute("SDATFiles", storageService.loadAll(FileType.SDAT).map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), "sdat").build().toUri().toString())
                .collect(Collectors.toList()));

        model.addAttribute("ESLFiles", storageService.loadAll(FileType.ESL).map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString(), "esl").build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{type}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, @PathVariable String type) {
        Resource file = storageService.loadAsResource(filename, FileType.fromString(type));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/{type}")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String type,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file, FileType.fromString(type));
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
