package ewm.user.controller;

import ewm.user.AdminUserParam;
import ewm.user.User;
import ewm.user.UserMapper;
import ewm.user.dto.NewUserRequest;
import ewm.user.dto.UserDto;
import ewm.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserRequest newUserRequest) {

        log.info("ADMIN: Получен запрос на создание пользователя {}", newUserRequest);
        User user = userService.create(UserMapper.fromNewUserRequest(newUserRequest));
        UserDto userDto = UserMapper.fromUserToUserDto(user);
        log.info("ADMIN: Новый пользователь создан: {}", userDto);
        return userDto;
    }

    @GetMapping
    public List<UserDto> getAllById(@RequestParam(required = false) List<Long> ids,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size
                                    ) {

        log.info("ADMIN: Получен запрос на получение всех пользователей по id {}, от {} до {}", ids, from, size);

        AdminUserParam adminUserParam = new AdminUserParam();
        adminUserParam.setIds(ids);
        adminUserParam.setFrom(from);
        adminUserParam.setSize(size);

        List<User> userList = userService.getAllByIds(adminUserParam);
        List<UserDto> userDtoList = UserMapper.fromListUserToListUserDto(userList);
        log.info("ADMIN: Получен список пользователей по запросу: {}", userDtoList);
        return userDtoList;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserDto delete(@PathVariable @Positive Long userId) {

        log.info("ADMIN: Получен запрос на удаление пользователя {}", userId);

        AdminUserParam adminUserParam = new AdminUserParam();
        adminUserParam.setUserId(userId);

        User user = userService.delete(adminUserParam);
        UserDto userDto = UserMapper.fromUserToUserDto(user);
        log.info("ADMIN: Пользователь по id {}", userId);
        return userDto;
    }











}















