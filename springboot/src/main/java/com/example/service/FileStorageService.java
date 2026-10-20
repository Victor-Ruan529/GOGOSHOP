package com.example.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.config.GogoshopProperties;
import com.example.common.enums.ResultCodeEnum;
import com.example.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 本地文件存储。安全措施：
 * 1. 只允许图片扩展名，并校验文件头（magic number），防止把 html/jsp/svg 伪装成图片上传；
 * 2. 存储文件名由服务端生成，不使用用户上传的原始文件名；
 * 3. 读取/删除时校验路径必须在上传目录内，防止 ../ 路径穿越。
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final GogoshopProperties properties;
    private Path root;

    public FileStorageService(GogoshopProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        String configured = properties.getFile().getUploadDir();
        if (StrUtil.isNotBlank(configured)) {
            root = Paths.get(configured);
        } else {
            // 兼容从项目根目录或 springboot 目录启动两种情况
            Path cwd = Paths.get(System.getProperty("user.dir"));
            root = Files.isDirectory(cwd.resolve("files")) ? cwd.resolve("files") : cwd.resolveSibling("files");
        }
        root = root.toAbsolutePath().normalize();
        FileUtil.mkdir(root.toFile());
        log.info("文件上传目录: {}", root);
    }

    /** 保存文件，返回可访问的 URL */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR.code, "请选择要上传的文件");
        }
        String ext = FileUtil.extName(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
        if (!properties.getFile().getAllowedExtensions().contains(ext) || !isImage(file)) {
            throw new CustomException(ResultCodeEnum.FILE_TYPE_ERROR);
        }
        String fileName = System.currentTimeMillis() + "-" + IdUtil.fastSimpleUUID().substring(0, 8) + "." + ext;
        try {
            file.transferTo(root.resolve(fileName));
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR.code, "文件上传失败");
        }
        return properties.getFile().getBaseUrl() + "/files/" + fileName;
    }

    /** 根据文件名解析出安全的本地路径，文件不存在或不合法时返回 null */
    public Path resolve(String fileName) {
        if (StrUtil.isBlank(fileName) || fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            return null;
        }
        String ext = FileUtil.extName(fileName).toLowerCase(Locale.ROOT);
        if (!properties.getFile().getAllowedExtensions().contains(ext)) {
            return null;
        }
        Path path = root.resolve(fileName).normalize();
        return path.startsWith(root) && Files.isRegularFile(path) ? path : null;
    }

    public void delete(String fileName) {
        Path path = resolve(fileName);
        if (path != null) {
            FileUtil.del(path);
        }
    }

    /** 校验文件头是否为常见图片格式 */
    static boolean isImage(MultipartFile file) {
        byte[] head = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int n = in.read(head);
            if (n < 4) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return startsWith(head, 0xFF, 0xD8, 0xFF)                       // jpg
                || startsWith(head, 0x89, 0x50, 0x4E, 0x47)             // png
                || startsWith(head, 0x47, 0x49, 0x46, 0x38)             // gif
                || startsWith(head, 0x42, 0x4D)                         // bmp
                || startsWith(head, 0x52, 0x49, 0x46, 0x46)             // webp: RIFF....WEBP
                && head[8] == 'W' && head[9] == 'E' && head[10] == 'B' && head[11] == 'P';
    }

    private static boolean startsWith(byte[] data, int... prefix) {
        for (int i = 0; i < prefix.length; i++) {
            if ((data[i] & 0xFF) != prefix[i]) {
                return false;
            }
        }
        return true;
    }
}
