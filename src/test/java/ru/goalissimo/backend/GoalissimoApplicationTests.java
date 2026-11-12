package ru.goalissimo.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Класс тестов для приложения Goalissimo.
 */
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
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
