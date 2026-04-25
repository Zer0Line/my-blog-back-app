package com.training.blog.controller;

import com.training.blog.service.FilesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import com.training.blog.dto.ImagePayload;

// import java.util.Map; // no longer needed

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FilesController.class)
class FilesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilesService filesService;

    @Test
    void uploadAndDownloadPostImage() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "post_image.png", "image/png", pngStub);

        doNothing().when(filesService).uploadPostImage(eq(1L), any());

        ByteArrayResource resource = new ByteArrayResource(pngStub);
        ImagePayload payload = new ImagePayload(MediaType.IMAGE_PNG, resource);
        when(filesService.downloadPostImage(1L)).thenReturn(Optional.of(payload));

        mockMvc.perform(multipart("/api/posts/1/image").file(file))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(pngStub));
    }
}