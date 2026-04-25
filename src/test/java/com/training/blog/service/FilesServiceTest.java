package com.training.blog.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilesServiceTest extends BaseServiceTest {

    @Autowired
    private FilesService filesService;

    @Test
    void getFileExtension_returnsExtension() throws Exception {
        FilesService service = new FilesService();
        java.lang.reflect.Method method = FilesService.class.getDeclaredMethod("getFileExtension", String.class);
        method.setAccessible(true);
        String ext = (String) method.invoke(service, "image.jpg");
        assertEquals(".jpg", ext);
    }

    @Test
    void getFileExtension_nullInput_returnsEmpty() throws Exception {
        FilesService service = new FilesService();
        java.lang.reflect.Method method = FilesService.class.getDeclaredMethod("getFileExtension", String.class);
        method.setAccessible(true);
        String ext = (String) method.invoke(service, (Object) null);
        assertEquals("", ext);
    }

    @Test
    void contextLoads() {
        assertNotNull(filesService);
    }

}