package com.project.MoveEnglish.entity.user;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface UserService {
    UserDto create(Long chatId, Update update);

    UserDto getById(Long chatId);

    List<UserDto> getAll();

    UserDto update(UserDto dto);

    void delete(Long chatId);

    Boolean existById(Long chatId);
}
