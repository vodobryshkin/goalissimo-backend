package ru.goalissimo.backend.users.application.usecase;

import ru.goalissimo.backend.users.application.dto.UserRegisterResult;
import ru.goalissimo.backend.users.application.exception.UserNameAlreadyExist;
import ru.goalissimo.backend.users.application.port.in.UserRegisterInPort;
import ru.goalissimo.backend.users.application.port.out.PasswordHasherOutPort;
import ru.goalissimo.backend.users.application.port.out.UserRegisterOutPort;
import ru.goalissimo.backend.users.domain.model.UserModel;

public class UserRegisterUseCase implements UserRegisterInPort {

    private final UserRegisterOutPort userRegisterOutPort;
    private final PasswordHasherOutPort passwordHasherOutPort;

    public UserRegisterUseCase(
            UserRegisterOutPort userRegisterOutPort,
            PasswordHasherOutPort passwordHasherOutPort
    ) {
        this.userRegisterOutPort = userRegisterOutPort;
        this.passwordHasherOutPort = passwordHasherOutPort;
    }

    @Override
    public UserRegisterResult register(String username, String password) {

        if (userRegisterOutPort.existsByUsername(username)) {
            throw new UserNameAlreadyExist("Пользователь с таким логином уже существует");
        }

        String passwordHash = passwordHasherOutPort.hash(password);
        UserModel saved = userRegisterOutPort.save(UserModel.register(username, passwordHash));

        return new UserRegisterResult(saved.id(), saved.username());
    }
}
