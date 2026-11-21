package ru.goalissimo.backend.users.application.exception;

public class UserNameAlreadyExist extends RuntimeException{

    public UserNameAlreadyExist(String message) {
        super(message);
    }
}
