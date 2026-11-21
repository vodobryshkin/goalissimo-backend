package ru.goalissimo.backend.users.domain.model;

/**
 * Учётная запись пользователя. Данные профиля заполняются после регистрации.
 *
 * @param id идентификатор пользователя, отсутствует до первого сохранения
 * @param username логин
 * @param fullName полное имя
 * @param gender пол
 * @param passwordHash хеш пароля
 * @param email адрес электронной почты
 * @param aboutDescription описание профиля
 * @param avatarLink ссылка на аватар
 */
public record UserModel(
        Long id,
        String username,
        String fullName,
        Gender gender,
        String passwordHash,
        String email,
        String aboutDescription,
        String avatarLink
) {

    /** Проверяет обязательные данные учётной записи. */
    public UserModel {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Логин обязателен");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Хеш пароля обязателен");
        }
    }

    /**
     * Создаёт учётную запись до сохранения и заполнения профиля.
     *
     * @param username логин
     * @param passwordHash хеш пароля
     * @return новая учётная запись
     */
    public static UserModel register(String username, String passwordHash) {
        return new UserModel(null, username, null, null, passwordHash, null, null, null);
    }

    /** Значения пола. */
    public enum Gender {
        MALE,
        FEMALE
    }
}
