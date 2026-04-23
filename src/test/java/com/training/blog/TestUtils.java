package com.training.blog;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtils {

    public static void executeSqlScript(JdbcTemplate jdbcTemplate, String scriptName) {
        assertNotNull(jdbcTemplate.getDataSource());
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
