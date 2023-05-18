package dev.groupb.m306groupb.storage;

import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.enums.SDATFileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void store(MultipartFile file, FileType fileType);

    Stream<Path> loadAll();

    Stream<Path> loadAll(FileType fileType);

    Path load(String filename, FileType fileType);

    Resource loadAsResource(String filename, FileType fileType);
}
