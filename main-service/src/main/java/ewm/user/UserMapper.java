package ewm.user;

import ewm.user.dto.NewUserRequest;
import ewm.user.dto.UserDto;
import ewm.user.dto.UserShortDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public User fromNewUserRequest(NewUserRequest newUserRequest) {

        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        return user;
    }

    public UserDto fromUserToUserDto(User user) {

        UserDto userDto = new UserDto();

        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        return userDto;
    }

    public UserShortDto fromUserToUserShortDto(User user) {

        UserShortDto userShortDto = new UserShortDto();

        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }

    public List<UserShortDto> fromUserListToListUserShortDto(List<User> userList) {

        return userList.stream()
                .map(UserMapper::fromUserToUserShortDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> fromListUserToListUserDto(List<User> userList) {

        return userList.stream()
                .map(UserMapper::fromUserToUserDto)
                .collect(Collectors.toList());
    }











}
