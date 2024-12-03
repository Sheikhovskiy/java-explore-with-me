package ewm.user.controller;

import ewm.event.dto.EventRequestStatusUpdateRequest;
import ewm.event.dto.EventRequestStatusUpdateResult;
import ewm.event.dto.EventRequestStatusUpdateShortResult;
import ewm.request.Request;
import ewm.request.RequestMapper;
import ewm.user.dto.ParticipationRequestDto;
import ewm.user.dto.UpdateEventUserRequest;
import ewm.event.EventMapper;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.model.Event;
import ewm.user.PrivateUserEventParam;
import ewm.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserEventController {

    private final UserService userService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUserEvent(@PathVariable Long userId,
                                        @RequestBody @Valid NewEventDto newEventDto) throws BadRequestException {

        log.info("PRIVATE: Получен запрос на создание событие {} от пользователя с id {}", newEventDto, userId);

        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEvent(EventMapper.toEventFromNewEventDto(newEventDto));
        privateUserEventParam.setCategory(newEventDto.getCategory());

        if (newEventDto.getEventDate() != null && fromStringToLocalDateTime(newEventDto.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата события не может быть в прошлом");
        }

        Event event = userService.createUserEvent(privateUserEventParam);
        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);
        log.info("PRIVATE: Создано событие {}, пользователем с id {}", eventFullDto, userId);

        return eventFullDto;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {

        log.info("PRIVATE: Получен запрос от пользователя с id {} по получению списка созданных им событий от {} размером {}", userId, from, size);

        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setFrom(from);
        privateUserEventParam.setSize(size);

        List<Event> eventList = userService.getEventsCreatedByUser(privateUserEventParam);
        List<EventShortDto> eventShortDtoList = EventMapper.toListEventShortDtoFromListEvent(eventList);

        log.info("PRIVATE: Получен список событий {}, созданных пользователем с id {}", eventShortDtoList, userId);
        return eventShortDtoList;
    }


    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {

        log.info("PRIVATE: Получен запрос на получение события с id {} от пользователя с id {}", userId, eventId);

        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);

        Event event = userService.getUserEvent(privateUserEventParam);
        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);
        log.info("PRIVATE: Получено событие {} от пользователя с id {}", userId, eventId);
        return eventFullDto;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) throws BadRequestException {

        log.info("PRIVATE: Получен запрос на изменение события {} с id {} от пользователя с id {}", updateEventUserRequest, eventId, userId);

        if (updateEventUserRequest.getEventDate() != null && fromStringToLocalDateTime(updateEventUserRequest.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата редактирования события не может быть раньше настоящей");
        }

        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);
        privateUserEventParam.setEvent(EventMapper.toEventFromUpdateEventUserRequest(updateEventUserRequest));
        privateUserEventParam.setCategory(updateEventUserRequest.getCategory());

        Event event = userService.updateUserEvent(privateUserEventParam);
        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);

        log.info("PRIVATE: Получено событие {} по id {} от пользователя с id {}", eventFullDto, eventId, userId);
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUserEvent(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {

        log.info("PRIVATE: Получен запрос на получение всех запросов по участию в событии с id {} от пользователя с id {}", eventId, userId);

        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);

        List<Request> requestList = userService.getRequestsOfUserEvent(privateUserEventParam);
        List<ParticipationRequestDto> participationRequestDtoList = RequestMapper.fromListRequestToListParticipationRequestDto(requestList);

        log.info("PRIVATE: Получен список запросов на участие {} в событии с id {}", participationRequestDtoList, eventId);
        return participationRequestDtoList;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                                      @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        log.info("PRIVATE: Получен запрос на обновление статуса запросов на участие в событии {} с id {} от пользователя с id {}", eventRequestStatusUpdateRequest, eventId, userId);
        PrivateUserEventParam privateUserEventParam = new PrivateUserEventParam();
        privateUserEventParam.setUserId(userId);
        privateUserEventParam.setEventId(eventId);
        privateUserEventParam.setEventRequestStatusUpdateRequest(eventRequestStatusUpdateRequest);

        EventRequestStatusUpdateShortResult eventRequestStatusUpdateShortResult = userService.updateParticipationRequests(privateUserEventParam);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = RequestMapper.fromEventRequestStatusUpdateShortResultToEventRequestStatusUpdateResult(eventRequestStatusUpdateShortResult);

        log.info("PRIVATE: Запрос на обновление статуса запросов на участие в событии {} с id {} от пользователя с id {} успешно выполнен!", eventRequestStatusUpdateResult, eventId, userId);
        return eventRequestStatusUpdateResult;
    }


    private LocalDateTime fromStringToLocalDateTime(String strDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return LocalDateTime.parse(strDate, formatter);
    }

}
