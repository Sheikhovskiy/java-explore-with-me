package ewm.user.service;

import com.querydsl.core.types.Predicate;
import ewm.exception.ConditionsNotRespected;
import ewm.user.User;
import ewm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final static String USER_EXISTS_BY_EMAIL = "Ошибка при работе с пользователями: " +
            "Пользователь с почтой {} уже существует!";

    @Override
    public User create(User user) {

        boolean emailExists = userRepository.existByEmail(user.getEmail());

        if (emailExists) {
            throw new ConditionsNotRespected(String.format(USER_EXISTS_BY_EMAIL, user.getEmail()));
        }
        return userRepository.save(user);
    }








}
