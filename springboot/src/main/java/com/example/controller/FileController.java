package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.PublicApi;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.RoleEnum;
import com.example.service.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * 文件接口：上传需要登录，读取公开，删除仅管理员
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @javax.annotation.Resource
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        return Result.success(fileStorageService.store(file));
    }

    /**
     * 读取图片：直接按图片类型返回（原来是 attachment 下载），并禁止浏览器猜测类型
     */
    @PublicApi
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> get(@PathVariable String fileName) {
        Path path = fileStorageService.resolve(fileName);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }
        MediaType type = MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(type)
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(new FileSystemResource(path));
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/{fileName:.+}")
    public Result delete(@PathVariable String fileName) {
        fileStorageService.delete(fileName);
        return Result.success();
    }
}
