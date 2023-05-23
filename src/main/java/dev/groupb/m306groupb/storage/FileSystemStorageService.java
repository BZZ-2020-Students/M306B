package dev.groupb.m306groupb.storage;

import dev.groupb.m306groupb.enums.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
    @Value("${sdat.files.path}")
    private String sdat_files_path;

    @Value("${esl.files.path}")
    private String esl_files_path;

    @Override
    public void store(MultipartFile file, FileType fileType) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            Path base_directory = switch (fileType) {
                case SDAT -> Paths.get(this.sdat_files_path);
                case ESL -> Paths.get(this.esl_files_path);
            };

            Path destinationFile = base_directory.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(base_directory.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll(FileType fileType) {
        Path base_directory = switch (fileType) {
            case SDAT -> Paths.get(this.sdat_files_path);
            case ESL -> Paths.get(this.esl_files_path);
        };
        try {
            return Files.walk(base_directory, 1)
                    .filter(path -> !path.equals(base_directory))
                    .map(base_directory::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return Arrays.stream(FileType.values())
                .flatMap(this::loadAll);
    }

    @Override
    public Path load(String filename, FileType fileType) {
        return switch (fileType) {
            case SDAT -> Paths.get(this.sdat_files_path).resolve(filename);
            case ESL -> Paths.get(this.esl_files_path).resolve(filename);
        };
    }

    @Override
    public Resource loadAsResource(String filename, FileType fileType) {
        try {
            Path file = load(filename, fileType);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
}
