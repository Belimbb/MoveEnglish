package com.project.MoveEnglish.entity;

import com.project.MoveEnglish.config.CustomMapperConfig;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = CustomMapperConfig.class)
public interface UserMapper {

    UserEntity toEntity(UserDto request);
    UserDto toDto(UserEntity userEntity);

    List<UserDto> toDtoList(List<UserEntity> entities);
    List<UserEntity> toEntityList(List<UserDto> dtos);
}