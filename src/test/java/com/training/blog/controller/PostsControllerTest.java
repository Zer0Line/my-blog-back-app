package com.training.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        // Cоздаёт mock-версию веб-слоя, без запуска реального сервера
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
class PostsControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        jdbcTemplate.execute("DROP TABLE IF EXISTS post_tags");
        jdbcTemplate.execute("DROP TABLE IF EXISTS comments");
        jdbcTemplate.execute("DROP TABLE IF EXISTS tags");
        jdbcTemplate.execute("DROP TABLE IF EXISTS posts");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("test-schema.sql"));
        populator.execute(dataSource);

        // Вставляем теги
        jdbcTemplate.execute("""
            INSERT INTO tags (name) VALUES
            ('tag1'), ('tag2'), ('other')
            """);

        // Вставляем посты
        jdbcTemplate.execute("""
            INSERT INTO posts (title, text, likes_count) VALUES
            ('Post 1', 'Text 1', 0),
            ('Post 2', 'Text 2', 0),
            ('Post 3', 'Text 3', 0),
            ('Post 4', 'Text 4', 0)
            """);

        // Связываем посты с тегами через post_tags
        // Post 1: tag1, tag2 | Post 2: tag1 | Post 3: tag2 | Post 4: other
        jdbcTemplate.execute("""
            INSERT INTO post_tags (post_id, tag_id) VALUES
            (1, 1),
            (1, 2),
            (2, 1),
            (3, 2),
            (4, 3)
            """);
    }

    @Test
    void getPosts() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts.length()").value(4));
    }

    @Test
    void getPost() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Text 1"));
    }

    @Test
    void createPost() throws Exception {
        String requestBody = """
                {
                    "title": "New Post",
                    "text": "Post content",
                    "tags": ["spring"]
                }
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.text").value("Post content"))
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void addLike() throws Exception {
        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1L));
    }
}