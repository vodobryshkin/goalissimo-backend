package ru.goalissimo.backend.users.application.port.out;

public interface PasswordHasherOutPort {

    String hash(String rawPassword);
}
