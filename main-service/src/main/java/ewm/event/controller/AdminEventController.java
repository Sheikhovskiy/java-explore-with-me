package ewm.event.controller;

import ewm.user.dto.UpdateEventAdminRequest;
import ewm.event.AdminEventParam;
import ewm.event.EventMapper;
import ewm.event.dto.EventFullDto;
import ewm.event.model.Event;
import ewm.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {

    private final EventService eventService;


    @GetMapping
    public List<EventFullDto> getEventsByParameters(@RequestParam(required = false) List<Long> users,
                                                    @RequestParam(required = false) List<String> states,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size
    ) {
        log.info("ADMIN: Получен запрос на получение всех событий от пользователей {}, с состоянием {}, по категориям {}, " +
                "от даты {} до {} от {} с количеством событий {}", users, states, categories, rangeStart, rangeEnd, from, size);
        AdminEventParam adminEventParam = new AdminEventParam();
        adminEventParam.setUsers(users);
        adminEventParam.setStates(states);
        adminEventParam.setCategories(categories);
        adminEventParam.setRangeStart(rangeStart);
        adminEventParam.setRangeEnd(rangeEnd);
        adminEventParam.setFrom(from);
        adminEventParam.setSize(size);

        List<Event> eventList = eventService.getAllByParameters(adminEventParam);
        List<EventFullDto> eventFullDtoList = EventMapper.toListEventFullDtoFromListEvent(eventList);
        log.info("ADMIN: Получен список событий по параметрам, {}", eventFullDtoList);
        return eventFullDtoList;

    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) throws BadRequestException {

        log.info("ADMIN: Получен запрос на изменение события и его статуса по id {}", eventId);

        if (updateEventAdminRequest.getEventDate() != null && fromStringToLocalDateTime(updateEventAdminRequest.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата редактирования события не может быть раньше настоящей");
        }

        AdminEventParam adminEventParam = new AdminEventParam();
        adminEventParam.setUpdateEventAdminRequest(updateEventAdminRequest);
        adminEventParam.setEventId(eventId);
        adminEventParam.setEvent(EventMapper.toEventFromUpdateEventAdminRequest(updateEventAdminRequest));
        adminEventParam.setCategory(updateEventAdminRequest.getCategory());

        Event event = eventService.update(adminEventParam);
        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);
        log.info("ADMIN: Событие {} успешно изменено !", eventFullDto);
        return eventFullDto;
    }




    private LocalDateTime fromStringToLocalDateTime(String strDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return LocalDateTime.parse(strDate, formatter);
    }



}
