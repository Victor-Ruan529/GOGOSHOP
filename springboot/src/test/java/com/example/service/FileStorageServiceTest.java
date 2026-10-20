package com.example.service;

import com.example.common.config.GogoshopProperties;
import com.example.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0, 0, 0};

    @TempDir
    Path dir;
    private FileStorageService service;

    @BeforeEach
    void setUp() {
        GogoshopProperties properties = new GogoshopProperties();
        properties.getFile().setUploadDir(dir.toString());
        properties.getFile().setAllowedExtensions(Arrays.asList("jpg", "png"));
        service = new FileStorageService(properties);
        service.init();
    }

    @Test
    void storesImageWithServerGeneratedName() {
        String url = service.store(new MockMultipartFile("file", "../../evil.png", "image/png", PNG));
        String name = url.substring(url.lastIndexOf('/') + 1);
        assertFalse(name.contains("evil"));
        assertTrue(Files.exists(dir.resolve(name)));
    }

    @Test
    void rejectsDisguisedOrDisallowedFiles() {
        assertThrows(CustomException.class,
                () -> service.store(new MockMultipartFile("file", "a.png", "image/png", "<script>".getBytes())));
        assertThrows(CustomException.class,
                () -> service.store(new MockMultipartFile("file", "a.html", "text/html", PNG)));
    }

    @Test
    void resolveBlocksPathTraversal() throws Exception {
        Files.write(dir.resolve("ok.png"), PNG);
        assertNotNull(service.resolve("ok.png"));
        assertNull(service.resolve("../ok.png"));
        assertNull(service.resolve("..%2Fok.png"));
        assertNull(service.resolve("sub/ok.png"));
        assertNull(service.resolve("missing.png"));
    }
}
