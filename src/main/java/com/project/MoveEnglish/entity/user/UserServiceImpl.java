package com.project.MoveEnglish.entity.user;

import com.project.MoveEnglish.exception.LogEnum;
import com.project.MoveEnglish.exception.generalExceptions.CustomAlreadyExistException;
import com.project.MoveEnglish.exception.generalExceptions.CustomNotFoundException;

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
        if (existById(chatId)){
            throw new CustomAlreadyExistException(OBJECT_NAME, chatId);
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
        //AppRegistry.addUserCompletely(user);

        log.info("{}: " + OBJECT_NAME + " (Username: {}) was created", LogEnum.SERVICE, userName);
        return saveUser(user);
    }

    @Override
    public UserDto getById(Long chatId) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by id {} was sent", LogEnum.SERVICE, chatId);
        return userMapper.toDto(findById(chatId));
    }

    @Override
    public List<UserDto> getAll() {
        log.info("{}: request on retrieving all " + OBJECT_NAME + "s was sent", LogEnum.SERVICE);
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto update(UserDto dto) {
        Long id = dto.id();
        UserEntity user = findById(id);
        user.setName(dto.name());
        user.setUsername(dto.username());

        log.info("{}: " + OBJECT_NAME + " (id: {}) was updated", LogEnum.SERVICE, id);
        return saveUser(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(findById(id));
        log.info("{}: " + OBJECT_NAME + " (id: {}) was deleted", LogEnum.SERVICE, id);
    }

    @Override
    public Boolean existById(Long id){
        return userRepository.existsById(id);
    }

    private UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, id));
    }

    private UserDto saveUser (UserEntity user){
        return userMapper.toDto(userRepository.save(user));
    }
}
