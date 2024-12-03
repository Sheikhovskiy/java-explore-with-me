package ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import ewm.StatClient;
import ewm.dto.HitCreateDto;
import ewm.dto.StatsCreateDto;
import ewm.dto.StatsDto;
import ewm.event.AdminEventParam;
import ewm.event.EventMapper;
import ewm.event.repository.EventRepository;
import ewm.event.EventState;
import ewm.event.PublicEventParam;
import ewm.event.model.Event;
import ewm.event.model.QEvent;
import ewm.event.repository.LocationRepository;
import ewm.exception.ConditionsNotRespected;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final StatClient statClient;

    private static final String NOT_EXISTING_EVENT = "Событие не найдено или недоступно";

    private static final String NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT = "Событие не удовлетворяет правилам редактирования";



    @Override
    public List<Event> getAllByParameters(AdminEventParam adminEventParam) {

        List<Long> userIds = adminEventParam.getUsers();

        EventState state = null;
        if (adminEventParam.getStates() != null) {
             state = adminEventParam.getStates().stream()
                    .map(EventState::from)
                    .findFirst()
                    .orElse(null);
        }

        List<Long> categories = adminEventParam.getCategories();
        String rangeStart = adminEventParam.getRangeStart();
        String rangeEnd = adminEventParam.getRangeEnd();
        int from = adminEventParam.getFrom();
        int size = adminEventParam.getSize();

        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();

        if (userIds != null && !userIds.isEmpty()) {
             predicate = predicate.and(qEvent.initiator.id.in(userIds));
        }

        if (state != null) {
            predicate =  predicate.and(qEvent.state.eq(state));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(qEvent.category.id.in(categories));
        }

        if (rangeStart != null) {
            predicate = predicate.and(qEvent.eventDate.after(fromStringToLocalDateTime(rangeStart)));
        }

        if (rangeEnd != null) {
            predicate = predicate.and(qEvent.eventDate.before(fromStringToLocalDateTime(rangeEnd)));
        }

        predicate = predicate.and(qEvent.id.gt(from));

        Pageable pageable = PageRequest.of(0, size);

        Iterable<Event> iterable = eventRepository.findAll(predicate, pageable);
        List<Event> eventList = new ArrayList<>();

        iterable.forEach(eventList::add);

        return eventList;
    }

    @Override
    public Event update(AdminEventParam adminEventParam) {

        Event newEvent = adminEventParam.getEvent();
        Long eventIdToModify = adminEventParam.getEventId();

        Optional<Event> eventOpt = eventRepository.findById(eventIdToModify);

        if (eventOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_EVENT);
        }

        Event eventExisting = eventOpt.get();

        Event eventToModify = EventMapper.toEventFromUpdateEventAdminRequest(eventExisting, newEvent);

        if (eventToModify.getPaid() != null) {
            eventExisting.setPaid(eventToModify.getPaid());
        }

        if (eventToModify.getEventDate() != null) {
            boolean respectedEventDate = eventExisting.getPublishedOn().plusHours(1).isBefore(eventToModify.getEventDate());
            if (!respectedEventDate) {
                throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
            }
        }

        if (eventExisting.getState() != null) {
            boolean respectedStateToPublish = eventExisting.getState().equals(EventState.PENDING);

            if (eventToModify.getState().equals(EventState.PUBLISH_EVENT) && !respectedStateToPublish) {
                throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
            }
        }

        if (eventExisting.getState() != null) {
            boolean isPublished = (eventExisting.getState().equals(EventState.PUBLISHED) && eventExisting.getRequestModeration());

            if (eventToModify.getState().equals(EventState.CANCELED) || isPublished) {
                throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_UPDATE_EVENT);
            }
        }

        locationRepository.save(eventToModify.getLocation());

        eventToModify.setId(eventIdToModify);

        if (eventToModify.getState().equals(EventState.PUBLISH_EVENT)) {
            eventToModify.setState(EventState.PUBLISHED);
        }

        if (eventToModify.getState().equals(EventState.REJECT_EVENT)) {
            if (eventExisting.getState().equals(EventState.PUBLISHED)) {
                throw new ConditionsNotRespected("Нельзя отменить уже опубликованное событие");
            }
            eventToModify.setState(EventState.CANCELED);
        }

        return eventRepository.save(eventToModify);
    }


    @Override
    public List<Event> getAllByPublicParameters(PublicEventParam publicEventParam) {

        String text = publicEventParam.getText();
        List<Long> categories = publicEventParam.getCategories();
        Boolean paid = publicEventParam.getPaid();
        String rangeStartStr = publicEventParam.getRangeStart();
        String rangeEndStr = publicEventParam.getRangeEnd();
        boolean onlyAvailable = publicEventParam.isOnlyAvailable();
        int from = publicEventParam.getFrom();
        int size = publicEventParam.getSize();

        EventState state = EventState.PUBLISHED;

        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();

        if (text != null && !text.isEmpty()) {
            StringExpression annotationLower = qEvent.annotation.lower();
            StringExpression descriptionLower = qEvent.description.lower();
            String textPattern = "%" + text.toLowerCase() + "%";
            predicate = predicate.and(
                    annotationLower.like(textPattern)
                            .or(descriptionLower.like(textPattern))
            );
        }


        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(qEvent.category.id.in(categories));
        }

        if (rangeStartStr != null) {
            predicate = predicate.and(qEvent.eventDate.after(fromStringToLocalDateTime(rangeStartStr)));
        }

        if (rangeEndStr != null) {
            predicate = predicate.and(qEvent.eventDate.before(fromStringToLocalDateTime(rangeEndStr)));
        }

        if (paid != null) {
            predicate = predicate.and(qEvent.paid.eq(paid));
        }

        predicate = predicate.and(qEvent.state.eq(state));


        if (onlyAvailable) {
            predicate = predicate.and(qEvent.participantLimit.gt(qEvent.confirmedRequests.size()));
        }

        predicate = predicate.and(qEvent.id.gt(from));

        Pageable pageable = PageRequest.of(0, size);

        Iterable<Event> eventIterable = eventRepository.findAll(predicate, pageable).getContent();
        List<Event> eventListResult = new ArrayList<>();
        eventIterable.forEach(eventListResult::add);

        return eventListResult;
    }

    @Override
    public Event getById(PublicEventParam publicEventParam) {

        Long eventId = publicEventParam.getEventId();
        String ip = publicEventParam.getIp();
        String uri = publicEventParam.getUri();

        Optional<Event> eventOpt = eventRepository.getEventById(eventId);

        if (eventOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_EVENT);
        }

        Event event = eventOpt.get();

        if (event.getState() != null && !event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format(NOT_EXISTING_EVENT, eventId));
        }

        StatsCreateDto statsCreateDto = new StatsCreateDto();
        statsCreateDto.setStart(fromLocalDateTimeToString(event.getCreatedOn()));
        statsCreateDto.setEnd(fromLocalDateTimeToString(LocalDateTime.now()));
        statsCreateDto.setUris(List.of(uri));
        statsCreateDto.setUnique(true);

        List<StatsDto> viewStatsDtoList = statClient.getStats(statsCreateDto);

        HitCreateDto hitCreateDto = new HitCreateDto();
        hitCreateDto.setApp("main-service");
        hitCreateDto.setUri(uri);
        hitCreateDto.setIp(ip);
        hitCreateDto.setTimestamp(fromLocalDateTimeToString(LocalDateTime.now()));

        statClient.sendHit(hitCreateDto);

        if (viewStatsDtoList.isEmpty()) {
            event.setViews(event.getViews() + 1);
        }

        return eventRepository.save(event);
    }






    private LocalDateTime fromStringToLocalDateTime(String strDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return LocalDateTime.parse(strDate, formatter);
    }

    private String fromLocalDateTimeToString(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return dateTime.format(formatter);
    }





}
