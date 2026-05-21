package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
@RequestMapping("/api/file")
class FileController {

    private static final Path UPLOAD_DIR = Path.of("uploads");

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Map.of("ok", false, "msg", "文件为空");

        Files.createDirectories(UPLOAD_DIR);
        String orig = file.getOriginalFilename();
        String ext = (orig != null && orig.lastIndexOf('.') >= 0) ? orig.substring(orig.lastIndexOf('.')) : "";
        String name = UUID.randomUUID() + ext;
        Path target = UPLOAD_DIR.resolve(name);
        file.transferTo(target.toAbsolutePath());

        return Map.of(
            "ok", true,
            "name", name,
            "size", file.getSize(),
            "url", "/api/file/" + name
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) {
        Path p = UPLOAD_DIR.resolve(name);
        if (!Files.exists(p)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
            .body(new FileSystemResource(p));
    }
}
