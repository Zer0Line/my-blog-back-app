package com.training.blog.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({PostService.class, CommentService.class, FilesService.class})
@ActiveProfiles("test")
public abstract class BaseServiceTest {

}
