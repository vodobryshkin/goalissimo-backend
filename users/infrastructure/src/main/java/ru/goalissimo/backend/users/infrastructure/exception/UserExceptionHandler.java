package ru.goalissimo.backend.users.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.goalissimo.backend.users.application.exception.UserNameAlreadyExist;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNameAlreadyExist.class)
    public ProblemDetail handleUsernameAlreadyExists(UserNameAlreadyExist exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
