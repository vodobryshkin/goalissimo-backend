package ru.goalissimo.backend.middleware.security;

import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Включает проверки доступа к методам Spring-компонентов.
 */
@Configuration(proxyBeanMethods = false)
// @EnableMethodSecurity
public class MethodSecurityConfiguration {
}
