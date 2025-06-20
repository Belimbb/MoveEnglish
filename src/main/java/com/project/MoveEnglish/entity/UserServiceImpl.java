package com.project.MoveEnglish.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final String OBJECT_NAME = "User";

    @Override
    public UserDto create(Long chatId, Update update) {
        if (chatId == null) {
            return null;
        }
        String firstName = "";
        String userName = "";
        if (update.hasMessage()) {
            firstName = update.getMessage().getFrom().getFirstName();
            userName = update.getMessage().getFrom().getLastName();
        }
        if (update.hasCallbackQuery()) {
            firstName = update.getCallbackQuery().getFrom().getFirstName();
            userName = update.getCallbackQuery().getFrom().getLastName();
        }
        UserEntity user = new UserEntity(chatId, firstName, userName);
        //log.info("addUser: {} {}", chatId, firstName);
        //AppRegistry.addUserCompletely(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getById(Long chatId) {
        return null;
    }

    @Override
    public List<UserDto> getAll() {
        return List.of();
    }

    @Override
    public UserDto update(UserDto dto) {
        return null;
    }

    @Override
    public void delete(UserDto id) {

    }
}
