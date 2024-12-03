package ewm.event.controller;

import ewm.StatClient;
import ewm.dto.HitCreateDto;
import ewm.event.EventMapper;
import ewm.event.PublicEventParam;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.model.Event;
import ewm.event.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    private final StatClient statClient;

    @GetMapping
    public List<EventShortDto> getAllEventsByParameters(@RequestParam(required = false) String text,
                                                        @RequestParam(required = false) @Size(min = 1) List<Long> categories,
                                                        @RequestParam(required = false) Boolean paid,
                                                        @RequestParam(required = false) String rangeStart,
                                                        @RequestParam(required = false) String rangeEnd,
                                                        @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                        @RequestParam(defaultValue = "VIEWS") String sort,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request) throws BadRequestException {

        log.info("PUBLIC: Получен запрос на получение события по тексту {}, в категориях {}, оплаченный {}, " +
                "от {} до {} только доступные: {}, по сортировке {}, от {} размером {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        if (categories != null && categories.contains(0L)) {
            throw new BadRequestException("Категория с id 0 не может существовать !");
        }

        PublicEventParam publicEventParam = new PublicEventParam();
        publicEventParam.setText(text);
        publicEventParam.setCategories(categories);
        publicEventParam.setPaid(paid);
        publicEventParam.setRangeStart(rangeStart);
        publicEventParam.setRangeEnd(rangeEnd);
        publicEventParam.setSort(sort);
        publicEventParam.setFrom(from);
        publicEventParam.setSize(size);

        List<Event> eventList = eventService.getAllByPublicParameters(publicEventParam);
        List<EventShortDto> eventShortDtoList = EventMapper.toListEventShortDtoFromListEvent(eventList);
        log.info("PUBLIC: Получен список всех событий по параметрам {}", eventShortDtoList);

        HitCreateDto hitCreateDto = new HitCreateDto();
        hitCreateDto.setApp("main-service");
        hitCreateDto.setUri(request.getRequestURI());
        hitCreateDto.setIp(request.getRemoteAddr());
        hitCreateDto.setTimestamp(fromLocalDateTimeToString(LocalDateTime.now()));
        statClient.sendHit(hitCreateDto);

        return eventShortDtoList;
    }


    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        log.info("PUBLIC: Получен запрос на получения события по id {}", id);

        log.info("PUBLIC: IP Клиента: {}", request.getRemoteAddr());
        log.info("PUBLIC: Путь Эндпоинта: {}", request.getRequestURI());

        PublicEventParam publicEventParam = new PublicEventParam();
        publicEventParam.setEventId(id);
        publicEventParam.setIp(request.getRemoteAddr());
        publicEventParam.setUri(request.getRequestURI());

        Event event = eventService.getById(publicEventParam);
        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);

        log.info("PUBLIC: Получено событие {} по id {}", eventFullDto, id);
        return eventFullDto;
    }






    private String fromLocalDateTimeToString(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return  dateTime.format(formatter);
    }


}
