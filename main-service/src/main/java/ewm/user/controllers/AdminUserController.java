package ewm.user.controllers;


import ewm.user.dto.NewUserRequest;
import ewm.user.dto.UserDto;
import ewm.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public List<UserDto> create(@RequestBody @Valid NewUserRequest newUserRequest) {
        return null;
    }


}
