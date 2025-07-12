package com.codewithmosh.store.Mappers;

import com.codewithmosh.store.DTO.RegisterUserRequest;
import com.codewithmosh.store.DTO.UpdateUserRequest;
import com.codewithmosh.store.DTO.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {
    @Override
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request,@MappingTarget User user);
}
