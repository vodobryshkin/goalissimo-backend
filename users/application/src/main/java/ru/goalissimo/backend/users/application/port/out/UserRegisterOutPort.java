package ru.goalissimo.backend.users.application.port.out;

import ru.goalissimo.backend.users.domain.model.UserModel;

public interface UserRegisterOutPort {

    boolean existsByUsername(String username);
    UserModel save(UserModel user);
}
