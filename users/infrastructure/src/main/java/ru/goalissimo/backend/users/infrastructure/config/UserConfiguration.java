package ru.goalissimo.backend.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.goalissimo.backend.users.application.port.in.UserRegisterInPort;
import ru.goalissimo.backend.users.application.port.out.PasswordHasherOutPort;
import ru.goalissimo.backend.users.application.port.out.UserRegisterOutPort;
import ru.goalissimo.backend.users.application.usecase.UserRegisterUseCase;
import ru.goalissimo.backend.users.infrastructure.adapter.out.Argon2idPasswordHasher;

@Configuration(proxyBeanMethods = false)
public class UserConfiguration {

    @Bean
    public UserRegisterInPort userRegisterInPort(
            UserRegisterOutPort users,
            PasswordHasherOutPort passwordHasher
    ) {
        return new UserRegisterUseCase(users, passwordHasher);
    }

    @Bean
    public PasswordHasherOutPort passwordHasher() {
        return new Argon2idPasswordHasher();
    }
}
