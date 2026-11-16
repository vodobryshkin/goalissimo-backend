package ru.goalissimo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.goalissimo.backend.middleware.config.MiddlewareConfiguration;

/**
 * Главный класс Spring Boot приложения Goalissimo.
 */
@SpringBootApplication
@Import(MiddlewareConfiguration.class)
@SuppressWarnings("PMD.UseUtilityClass")
public class GoalissimoApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(GoalissimoApplication.class, args);
    }
}