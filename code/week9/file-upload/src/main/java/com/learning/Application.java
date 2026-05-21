package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// FileSystemResource：把磁盘文件包装成 Spring Resource
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
// HTTP 头常量
import org.springframework.http.HttpHeaders;
// ResponseEntity 让你完全控制响应（状态码 / 头 / body）
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// 上传文件的 Spring 封装类
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

    // 文件存放目录（相对工作目录），生产应放绝对路径或对象存储
    private static final Path UPLOAD_DIR = Path.of("uploads");

    /**
     * 上传：用 multipart/form-data 表单上传
     *
     * @RequestParam("file") 对应表单字段名 file
     * MultipartFile 是 Spring 提供的"上传文件"封装
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 空文件直接拒绝
        if (file.isEmpty()) return Map.of("ok", false, "msg", "文件为空");

        // 确保目录存在（已存在不报错）
        Files.createDirectories(UPLOAD_DIR);

        // 原始文件名（可能为 null，比如直接 binary 上传）
        String orig = file.getOriginalFilename();

        // 提取扩展名：找最后一个 . 之后的部分
        // 三元 + null 检查防止 NullPointerException
        String ext = (orig != null && orig.lastIndexOf('.') >= 0) ? orig.substring(orig.lastIndexOf('.')) : "";

        // 用 UUID 防止文件名冲突（用户上传同名文件不互相覆盖）
        String name = UUID.randomUUID() + ext;

        // 拼路径并把上传内容写进去
        Path target = UPLOAD_DIR.resolve(name);
        file.transferTo(target.toAbsolutePath());

        // Map.of(key, value, key, value...) 创建小 Map（JDK 9+）
        return Map.of(
            "ok", true,
            "name", name,
            "size", file.getSize(),
            "url", "/api/file/" + name      // 下载 URL
        );
    }

    /**
     * 下载：浏览器访问会触发文件下载
     */
    @GetMapping("/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) {
        Path p = UPLOAD_DIR.resolve(name);
        if (!Files.exists(p)) return ResponseEntity.notFound().build();

        // ResponseEntity 设响应头让浏览器下载而非预览
        // Content-Disposition: attachment; filename=xxx → 强制下载
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
            .body(new FileSystemResource(p));
    }
}
