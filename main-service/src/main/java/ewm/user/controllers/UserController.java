package ewm.user.controllers;


import ewm.user.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {


    @PostMapping
    public UserDto createUser() {

    }


}
