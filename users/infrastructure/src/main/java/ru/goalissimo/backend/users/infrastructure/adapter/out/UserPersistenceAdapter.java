package ru.goalissimo.backend.users.infrastructure.adapter.out;

import org.springframework.stereotype.Repository;
import ru.goalissimo.backend.users.application.port.out.UserRegisterOutPort;
import ru.goalissimo.backend.users.domain.model.UserModel;
import ru.goalissimo.backend.users.infrastructure.persistence.entity.UserEntity;
import ru.goalissimo.backend.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.goalissimo.backend.users.infrastructure.persistence.repository.UserRepository;

@Repository
public class UserPersistenceAdapter implements UserRegisterOutPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(
            UserRepository userRepository,
            UserPersistenceMapper userPersistenceMapper
    ) {
        this.userRepository = userRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserModel save(UserModel user) {
        UserEntity entity = userPersistenceMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return userPersistenceMapper.toModel(saved);
    }
}
