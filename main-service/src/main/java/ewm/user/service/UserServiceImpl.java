package ewm.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.category.Category;
import ewm.category.CategoryRepository;
import ewm.event.repository.EventRepository;
import ewm.event.EventState;
import ewm.event.dto.EventRequestStatusUpdateRequest;
import ewm.event.dto.EventRequestStatusUpdateShortResult;
import ewm.event.model.Event;
import ewm.event.model.Location;
import ewm.event.model.QEvent;
import ewm.event.repository.LocationRepository;
import ewm.request.QRequest;
import ewm.request.Request;
import ewm.exception.ConditionsNotRespected;
import ewm.exception.NotFoundException;
import ewm.request.RequestRepository;
import ewm.request.RequestStatus;
import ewm.user.AdminUserParam;
import ewm.user.PrivateUserEventParam;
import ewm.user.PrivateUserRequestParam;
import ewm.user.QUser;
import ewm.user.User;
import ewm.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final static String USER_EXISTS_BY_EMAIL = "Ошибка при работе с пользователями: " +
            "Пользователь с почтой {} уже существует!";

    private final static String NOT_EXISTING_USER = "Пользователь не найден";

    private final static String NOT_EXISTING_EVENT = "Событие не найдено или недоступно";

    private final static String ALREADY_EXISTING_REQUEST_FOR_EVENT = "Заявка на участие в мероприятие по id %d уже существует !";

    private final static String NOT_PERMITTED_TO_APPLY_TO_PARTICIPATE_AT_OWN_EVENT = "Инициатор с id %d события с id %d не может добавить запрос на участие в своём событии";

    private final static String NOT_PERMITTED_TO_APPLY_TO_NOT_PUBLISHED_EVENT = "Нельзя участвовать в неопубликованном событии с id %d";

    private final static String EVENT_REACHED_MAX_PARTICIPANTS = "У события с id %d достигнут лимит запросов на участие";

    private final static String NOT_EXISTING_REQUEST = "Запрос с id %d не найден или не существует";

    private final static String NOT_EXISTING_CATEGORY = "Ошибка при работе с пользователями: Категория по id %d " +
            ", которую вы пытаетесь использовать не существует !";

    private final static String NOT_RESPECTED_CONDITIONS_TO_MODIFY_USER_EVENT = "Изменить можно только отмененные события или события в состоянии ожидания модерации";

    private final static String NOT_RESPECTED_TIME_RULES_TO_MODIFY_USER_EVENT = "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента";


    @Override
    public User create(User user) {

        boolean emailExists = userRepository.existByEmail(user.getEmail());

        if (emailExists) {
            throw new ConditionsNotRespected(String.format(USER_EXISTS_BY_EMAIL, user.getEmail()));
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllByIds(AdminUserParam adminUserParam) {

        List<Long> ids = adminUserParam.getIds();
        int from = adminUserParam.getFrom();
        int size = adminUserParam.getSize();

        QUser qUser = QUser.user;

        BooleanExpression predicate = qUser.isNotNull();
        if (ids != null && !ids.isEmpty()) {
            predicate = predicate.and(qUser.id.in(ids));
        }

        predicate = predicate.and(qUser.id.gt(from));

        Pageable pageable = PageRequest.of(0, size);

        Iterable<User> userIterable = userRepository.findAll(predicate, pageable);
        List<User> userList = new ArrayList<>();
        userIterable.forEach(userList::add);

        return userList;

    }

    @Override
    public User delete(AdminUserParam adminUserParam) {

        Optional<User> userOpt = userRepository.getUserById(adminUserParam.getUserId());

        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_USER, adminUserParam.getUserId()));
        }

        userRepository.delete(userOpt.get());
        return userOpt.get();
    }

    @Override
    public Request createUserRequest(PrivateUserRequestParam privateUserRequestParam) {

        Long userId = privateUserRequestParam.getUserId();
        Long eventId = privateUserRequestParam.getEventId();

        Optional<User> userOpt = userRepository.getUserById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_USER);
        }

        Optional<Event> eventOpt = eventRepository.getEventById(eventId);

        if (eventOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_EVENT);
        }

        Event event = eventOpt.get();

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotRespected(String.format(NOT_PERMITTED_TO_APPLY_TO_PARTICIPATE_AT_OWN_EVENT, userId, eventId));
        }

        if (!event.getState().equals(EventState.PUBLISHED) && !event.getState().equals(EventState.PUBLISH_EVENT)) {
            throw new ConditionsNotRespected(String.format(NOT_PERMITTED_TO_APPLY_TO_NOT_PUBLISHED_EVENT, eventId));
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(Long.valueOf(event.getConfirmedRequests().size()))) {
            throw new ConditionsNotRespected(String.format(EVENT_REACHED_MAX_PARTICIPANTS, eventId));
        }

        boolean requestAlreadyExist = requestRepository.requestExists(userId, eventId);

        if (requestAlreadyExist) {
            throw new ConditionsNotRespected(String.format(ALREADY_EXISTING_REQUEST_FOR_EVENT, eventId));
        }

        QRequest qRequest = QRequest.request;
        BooleanExpression predicate = qRequest.event.id.eq(eventId);
        predicate = predicate.and(qRequest.status.eq(RequestStatus.CONFIRMED));

        Iterable<Request> requestIterable = requestRepository.findAll(predicate);
        List<Request> requestList = new ArrayList<>();
        requestIterable.forEach(requestList::add);

        if (event.getParticipantLimit() != 0 && requestList.size() == event.getParticipantLimit()) {
            throw new ConditionsNotRespected("Достигнут лимит запросов на участие в событии");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventOpt.get());
        request.setRequester(userOpt.get());

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        Request requestSaved = requestRepository.save(request);

        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            System.out.println("EVENT BEFORE " + event.getConfirmedRequests());
            event.getConfirmedRequests().add(requestSaved.getId());
            System.out.println("EVENT AFTER " + event.getConfirmedRequests());
        }

        return requestSaved;
    }


    @Override
    public List<Request> getUserRequests(Long userId) {

        User user = checkIfUserExists(userId);

        Optional<List<Request>> requestOpt = requestRepository.getRequestsByRequesterId(userId);

        if (requestOpt.isEmpty()) {
            return new ArrayList<>();
        }

        return requestOpt.get();
    }

    @Override
    public Request cancelUserRequest(PrivateUserRequestParam privateUserRequestParam) {

        Long userId = privateUserRequestParam.getUserId();
        Long requestId = privateUserRequestParam.getRequestId();

        User user = checkIfUserExists(userId);

        Optional<Request> requestOpt = requestRepository.getRequestById(requestId);

        if (requestOpt.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_REQUEST, requestId));
        }

        Request requestToModify = requestOpt.get();
        requestToModify.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(requestToModify);
    }

    @Override
    public List<Event> getEventsCreatedByUser(PrivateUserEventParam privateUserEventParam) {

        QUser qUser = QUser.user;
        QEvent qEvent = QEvent.event;

        Long userId = privateUserEventParam.getUserId();
        int from = privateUserEventParam.getFrom();
        int size = privateUserEventParam.getSize();

        User user = checkIfUserExists(userId);

        BooleanExpression byUserId = qEvent.initiator.id.eq(userId);
        BooleanExpression fromQ = qEvent.id.gt(from);
        Pageable pageable = PageRequest.of(0, size);

        Iterable<Event> eventIterable = eventRepository.findAll(byUserId.and(fromQ), pageable);
        List<Event> eventList = new ArrayList<>();

        eventIterable.forEach(eventList::add);
        return eventList;
    }

    @Override
    public Event createUserEvent(PrivateUserEventParam privateUserEventParam) {

        Long userId = privateUserEventParam.getUserId();
        Long categoryId = privateUserEventParam.getCategory();
        Event event = privateUserEventParam.getEvent();

        Location location = event.getLocation();

        User user = checkIfUserExists(userId);

        Category category = checkIfCategoryExists(categoryId);

        event.setInitiator(user);
        event.setCategory(category);
        event.setConfirmedRequests(new ArrayList<>());
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(LocalDateTime.now());
        event.setViews(0L);

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }

        event.setState(EventState.PENDING);

        locationRepository.save(location);
        return eventRepository.save(event);
    }

    @Override
    public Event getUserEvent(PrivateUserEventParam privateUserEventParam) {

        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();

        User user = checkIfUserExists(userId);

        return checkIfEventExists(eventId);

    }

    @Override
    public Event updateUserEvent(PrivateUserEventParam privateUserEventParam) {

        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();
        Long categoryId = privateUserEventParam.getCategory();
        Event eventToModify = privateUserEventParam.getEvent();

        User user = checkIfUserExists(userId);
        Event eventExisting = checkIfEventExists(eventId);

        if (categoryId != null) {
            Category category = checkIfCategoryExists(categoryId);
        }

        for (Field field : Event.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object valueToModify = field.get(eventToModify);
                Object valueExisting = field.get(eventExisting);

                if (valueToModify == null && valueExisting != null) {
                    field.set(eventToModify, valueExisting);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }


        if (!eventExisting.getState().equals(EventState.PENDING) && !eventExisting.getState().equals(EventState.CANCELED)) {
            throw new ConditionsNotRespected(NOT_RESPECTED_CONDITIONS_TO_MODIFY_USER_EVENT);
        }

        if (!eventExisting.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
            throw new ConditionsNotRespected(NOT_RESPECTED_TIME_RULES_TO_MODIFY_USER_EVENT);
        }

        eventToModify.setId(eventExisting.getId());
        eventToModify.setInitiator(user);

        if (eventToModify.getState() != null && eventToModify.getState().equals(EventState.CANCEL_REVIEW)) {
            eventToModify.setState(EventState.CANCELED);
        } else {
            eventToModify.setState(EventState.PENDING);
        }
        eventToModify.setPaid(eventExisting.getPaid());
        eventToModify.setRequestModeration(eventExisting.getRequestModeration());
        return eventRepository.save(eventToModify);
    }

    @Override
    public List<Request> getRequestsOfUserEvent(PrivateUserEventParam privateUserEventParam) {

        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();

        User owner = checkIfUserExists(userId);
        Event event = checkIfEventExists(eventId);

        return requestRepository.getRequestByEventId(eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateShortResult updateParticipationRequests(PrivateUserEventParam privateUserEventParam) {

        Long userId = privateUserEventParam.getUserId();
        Long eventId = privateUserEventParam.getEventId();
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = privateUserEventParam.getEventRequestStatusUpdateRequest();

        List<Long> requestsId = eventRequestStatusUpdateRequest.getRequestIds();
        RequestStatus requestStatus = RequestStatus.from(eventRequestStatusUpdateRequest.getStatus());

        User owner = checkIfUserExists(userId);
        Event event = checkIfEventExists(eventId);

        Long limit = event.getParticipantLimit();
        Long confirmedRequests = (long) event.getConfirmedRequests().size();


        if (limit.equals(confirmedRequests) && requestStatus.equals(RequestStatus.CONFIRMED)) {
            throw new ConditionsNotRespected("Нельзя принять запрос на участие в событии т.к лимит участников уже достигнут!");
        }

        long updatedRows = updateRequestStatus(requestsId, requestStatus);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Ни один запрос не был обновлён");
        }

        List<Long> confirmedExistingRequests = event.getConfirmedRequests();
        if (requestStatus.equals(RequestStatus.REJECTED)) {
            for (Long id : requestsId) {
                if (confirmedExistingRequests.contains(id)) {
                    throw new ConditionsNotRespected("Нельзя отменить уже принятую заявку");
                }
            }
        }


        QRequest qRequest = QRequest.request;
        BooleanExpression predicate = qRequest.id.in(requestsId);
        predicate = predicate.and(qRequest.status.eq(RequestStatus.CONFIRMED));

        Iterable<Request> requestsToUpdate  = requestRepository.findAll(predicate);

        for (Request request : requestsToUpdate) {
            if (request.getStatus() == RequestStatus.CONFIRMED && requestStatus == RequestStatus.REJECTED) {
                throw new ConditionsNotRespected("Нельзя изменить статус с CONFIRMED на REJECTED");
            }
        }

        List<Request> updatedRequestsList = new ArrayList<>();
        requestsToUpdate.forEach(updatedRequestsList::add);
        List<Long> requestsIds = updatedRequestsList.stream().map(Request::getId).toList();

        EventRequestStatusUpdateShortResult result = new EventRequestStatusUpdateShortResult();

        if (requestStatus.equals(RequestStatus.CONFIRMED)) {
            if (event.getConfirmedRequests() == null) {
                List<Long> confirmed = new ArrayList<>(requestsIds);
                event.setConfirmedRequests(confirmed);
                result.setConfirmedRequests(updatedRequestsList);
            } else {
                List<Long> confirmedAlready = event.getConfirmedRequests();
                List<Long> confirmed = new ArrayList<>(requestsIds);
                confirmed.addAll(confirmedAlready);
                event.setConfirmedRequests(confirmed);
                result.setConfirmedRequests(updatedRequestsList);
            }
            requestsToUpdate.forEach(rq -> rq.setStatus(RequestStatus.CONFIRMED));

            int num = requestRepository.updateRequestStatus(requestsIds, requestStatus);

        } else {
            QRequest qRequest1 = QRequest.request;
            BooleanExpression predicate1 = qRequest1.id.in(requestsId);
            predicate1 = predicate1.and(qRequest1.status.eq(RequestStatus.REJECTED));

            Iterable<Request> requestIterable = requestRepository.findAll(predicate1);
            List<Request> requestList = new ArrayList<>();
            requestIterable.forEach(requestList::add);

            result.setRejectedRequests(requestList);

            int num = requestRepository.updateRequestStatus(requestsIds, requestStatus);

        }

        eventRepository.save(event);
        return result;
    }


    @Transactional
    public long updateRequestStatus(List<Long> requestIds, RequestStatus status) {
        QRequest qRequest = QRequest.request;

        BooleanExpression inRequests = qRequest.id.in(requestIds);

        Iterable<Request> requestsToUpdate = requestRepository.findAll(inRequests);

        List<Request> updatedRequests = new ArrayList<>();
        requestsToUpdate.forEach(request -> {
            request.setStatus(status);
            updatedRequests.add(request);
        });

        requestRepository.saveAll(updatedRequests);
        return updatedRequests.size();
    }










    private User checkIfUserExists(Long userId) {
        Optional<User> userOpt = userRepository.getUserById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_USER);
        }

        return userOpt.get();
    }

    private Event checkIfEventExists(Long eventId) {
        Optional<Event> eventOpt = eventRepository.getEventById(eventId);

        if (eventOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_EVENT);
        }

        return eventOpt.get();
    }

    private Category checkIfCategoryExists(Long categoryId) {
        Optional<Category> categoryOpt = categoryRepository.getByCategoryId(categoryId);

        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_CATEGORY, categoryId));
        }

        return categoryOpt.get();
    }

}
