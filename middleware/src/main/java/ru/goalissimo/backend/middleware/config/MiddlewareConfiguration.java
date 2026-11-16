package ru.goalissimo.backend.middleware.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.goalissimo.backend.middleware.security.MethodSecurityConfiguration;

/**
 * Общие технические конфигурации приложения.
 *
 * @author vodobryshkin
 * @since 16.11.2026 11:47
 */
@Configuration(proxyBeanMethods = false)
@Import(MethodSecurityConfiguration.class)
public class MiddlewareConfiguration {
}
