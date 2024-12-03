package ewm.user.controller;

import ewm.request.Request;
import ewm.request.RequestMapper;
import ewm.user.PrivateUserRequestParam;
import ewm.user.dto.ParticipationRequestDto;
import ewm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserRequestController {

    private final UserService userService;


    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable Long userId,
                                                     @RequestParam Long eventId) {

        log.info("PRIVATE: Получен запрос на участие пользователя по id {} в событие по id {}", userId, eventId);
        PrivateUserRequestParam privateUserRequestParam = new PrivateUserRequestParam();
        privateUserRequestParam.setUserId(userId);
        privateUserRequestParam.setEventId(eventId);

        Request request = userService.createUserRequest(privateUserRequestParam);
        ParticipationRequestDto participationRequestDto = RequestMapper.fromRequestToParticipationRequestDto(request);

        log.info("PRIVATE: Создан запрос на участие пользователя с id {} в событие {} с id {}", userId, participationRequestDto, eventId);
        return participationRequestDto;
    }


    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {

        log.info("PRIVATE: Получен запрос на получение информации о запросе на участие пользователя по id {}", userId);
        List<Request> requestList = userService.getUserRequests(userId);
        List<ParticipationRequestDto> participationRequestDtoList = RequestMapper.fromListRequestToListParticipationRequestDto(requestList);

        log.info("PRIVATE: Получен список запросов на участие {} пользователя по id {}", requestList, userId);
        return participationRequestDtoList;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserParticipation(@PathVariable Long userId,
                                                           @PathVariable Long requestId) {

        log.info("PRIVATE: Получен запрос на отмену запроса на участие в событие");

        PrivateUserRequestParam privateUserRequestParam = new PrivateUserRequestParam();
        privateUserRequestParam.setUserId(userId);
        privateUserRequestParam.setRequestId(requestId);

        Request request = userService.cancelUserRequest(privateUserRequestParam);
        ParticipationRequestDto participationRequestDto = RequestMapper.fromRequestToParticipationRequestDto(request);

        log.info("PRIVATE: Получен запрос на участие {} пользователя с id {}", participationRequestDto, userId);
        return participationRequestDto;
    }



}
