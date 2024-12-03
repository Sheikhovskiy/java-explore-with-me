package ewm.event;

import ewm.user.dto.UpdateEventAdminRequest;
import ewm.category.CategoryMapper;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.model.Event;
import ewm.user.UserMapper;
import ewm.user.dto.UpdateEventUserRequest;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public EventFullDto toEventFullDtoFromEvent(Event event) {

        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDtoFromCategory(event.getCategory()));
        if (!event.getConfirmedRequests().isEmpty()) {
            eventFullDto.setConfirmedRequests(event.getConfirmedRequests().size());
        } else {
            eventFullDto.setConfirmedRequests(0);
        }
        eventFullDto.setCreatedOn(fromLocalDateTimeToString(event.getCreatedOn()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(fromLocalDateTimeToString(event.getEventDate()));
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.fromUserToUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(LocationMapper.toLocationDtoFromLocation(event.getLocation()));
        if (event.getPaid() != null) {
            eventFullDto.setPaid(event.getPaid());
        }
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(fromLocalDateTimeToString(event.getPublishedOn()));
        if (event.getRequestModeration() != null) {
            eventFullDto.setRequestModeration(event.getRequestModeration());
        }
        eventFullDto.setState(EventState.to(event.getState()));
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public List<EventFullDto> toListEventFullDtoFromListEvent(List<Event> eventList) {

        return eventList.stream()
                .map(EventMapper::toEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    public Event toEventFromUpdateEventAdminRequest(UpdateEventAdminRequest updateEventAdminRequest) {

        Event event = new Event();
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
//        event.setCategory(updateEventAdminRequest.getCategory());
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(fromStringToLocalDateTime(updateEventAdminRequest.getEventDate()));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        event.setPaid(updateEventAdminRequest.getPaid());
        event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        event.setRequestModeration(updateEventAdminRequest.getRequestModeration());

        if (updateEventAdminRequest.getStateAction() != null) {
            event.setState(EventState.from(updateEventAdminRequest.getStateAction()));
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return event;
    }


    public EventShortDto toEventShortDtoFromEvent(Event event) {

        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDtoFromCategory(event.getCategory()));
        eventShortDto.setConfirmedRequests((long) event.getConfirmedRequests().size());
        eventShortDto.setEventDate(fromLocalDateTimeToString(event.getEventDate()));
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.fromUserToUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public List<EventShortDto> toListEventShortDtoFromListEvent(List<Event> eventList) {

        return eventList.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    public Event toEventFromNewEventDto(NewEventDto newEventDto) {

        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
//        event.setCategory(newEventDto.getCategory());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(fromStringToLocalDateTime(newEventDto.getEventDate()));
        event.setLocation(newEventDto.getLocation());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        }
        event.setParticipantLimit(newEventDto.getParticipantLimit());

        event.setRequestModeration(newEventDto.getRequestModeration());

        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public Event toEventFromUpdateEventUserRequest(UpdateEventUserRequest updateEventUserRequest) {

        Event event = new Event();
        event.setAnnotation(updateEventUserRequest.getAnnotation());
//        event.setCategory(updateEventUserRequest.getCategory());
        event.setDescription(updateEventUserRequest.getDescription());
        if (event.getEventDate() != null) {
            event.setEventDate(fromStringToLocalDateTime(updateEventUserRequest.getEventDate()));
        }
        event.setLocation(updateEventUserRequest.getLocation());
        event.setPaid(updateEventUserRequest.isPaid());
        event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        event.setRequestModeration(updateEventUserRequest.isRequestModeration());

        if (updateEventUserRequest.getStateAction() != null) {
            event.setState(EventState.from(updateEventUserRequest.getStateAction()));
        }
        event.setTitle(updateEventUserRequest.getTitle());

        return event;
    }












    private LocalDateTime fromStringToLocalDateTime(String strDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
        return dateTime;
    }

    private String fromLocalDateTimeToString(LocalDateTime localDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.from(localDateTime);
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }


}
