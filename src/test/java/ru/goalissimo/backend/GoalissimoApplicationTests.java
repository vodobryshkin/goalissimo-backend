package ru.goalissimo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс тестов для приложения Goalissimo.
 */
@SpringBootTest
class GoalissimoApplicationTests {

    /**
     * Тест для проверки загрузки контекста Spring Boot приложения.
     *
     * @param context контекст приложения, созданный для теста
     */
    @Test
    void contextLoads(ConfigurableApplicationContext context) {
        assertThat(context.isActive())
                .as("После запуска приложения Spring-контекст должен быть активен")
                .isTrue();
    }

}
