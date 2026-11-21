package ru.goalissimo.backend.users.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.goalissimo.backend.users.domain.model.UserModel.Gender;
import ru.goalissimo.backend.users.domain.model.UserModel;
import ru.goalissimo.backend.users.infrastructure.persistence.entity.UserEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserPersistenceMapper {

    UserEntity toEntity(UserModel user);

    UserModel toModel(UserEntity entity);

    default String toDatabaseGender(Gender gender) {
        return gender == null ? null : gender.name().toLowerCase();
    }

    default Gender toModelGender(String gender) {
        return gender == null ? null : Gender.valueOf(gender.toUpperCase());
    }
}
