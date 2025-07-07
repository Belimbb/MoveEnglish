package com.project.MoveEnglish.entity.user;

import com.project.MoveEnglish.config.CustomMapperConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", config = CustomMapperConfig.class)
public interface UserMapper {

    UserEntity toEntity(UserDto request);
    UserDto toDto(UserEntity userEntity);

    List<UserDto> toDtoList(List<UserEntity> entities);
    List<UserEntity> toEntityList(List<UserDto> dtos);
}