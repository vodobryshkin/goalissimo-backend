package ru.goalissimo.backend.users.infrastructure.adapter.out;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import ru.goalissimo.backend.users.application.port.out.PasswordHasherOutPort;

public class Argon2idPasswordHasher implements PasswordHasherOutPort {

    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BYTES = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY_KIB = 19 * 1024;
    private static final int ITERATIONS = 2;

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
            SALT_LENGTH_BYTES,
            HASH_LENGTH_BYTES,
            PARALLELISM,
            MEMORY_KIB,
            ITERATIONS
    );

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
