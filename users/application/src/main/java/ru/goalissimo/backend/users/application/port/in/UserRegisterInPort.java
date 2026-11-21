package ru.goalissimo.backend.users.application.port.in;

import ru.goalissimo.backend.users.application.dto.UserRegisterResult;

public interface UserRegisterInPort {

    UserRegisterResult register(String username, String password);
}
