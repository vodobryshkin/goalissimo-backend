package ru.goalissimo.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Тесты запуска приложения Goalissimo.
 */
@Testcontainers
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class GoalissimoApplicationTests {

    /**
     * Отдельная база данных для тестов.
     */
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    /**
     * Проверяет успешную загрузку контекста приложения.
     *
     * @param context контекст приложения
     */
    @Test
    void contextLoads(ConfigurableApplicationContext context) {
        assertThat(context.isActive())
                .as("После запуска приложения Spring-контекст должен быть активен")
                .isTrue();
    }
}