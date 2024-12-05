package ewm.subscription;

import ewm.event.EventMapper;
import ewm.event.dto.EventFullDto;
import ewm.event.model.Event;
import ewm.event.service.EventService;
import ewm.subscription.dto.SubscriptionDto;
import ewm.subscription.dto.SubscriptionRequestDto;
import ewm.subscription.dto.UpdateRequestStatus;
import ewm.subscription.model.Subscription;
import ewm.subscription.model.SubscriptionRequest;
import ewm.subscription.service.SubscriptionService;
import ewm.user.User;
import ewm.user.UserMapper;
import ewm.user.dto.UserShortDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    private final EventService eventService;


    @PostMapping("/{requesterId}/follow/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionRequestDto createFollowRequest(@PathVariable Long requesterId, @PathVariable Long userId) {

        log.info("SUBSCRIPTION: Получен запрос на подписку на пользователя с id {} от пользователя с id {}", userId, requesterId);
        SubscriptionRequestParam subscriptionRequestParam = new SubscriptionRequestParam();
        subscriptionRequestParam.setFollowerId(requesterId);
        subscriptionRequestParam.setFollowingId(userId);

        SubscriptionRequest subscriptionRequest = subscriptionService.createFollow(subscriptionRequestParam);
        SubscriptionRequestDto subscriptionRequestDto = SubscriptionMapper.toSubscriptionRequestDtoFromSubscriptionRequest(subscriptionRequest);
        log.info("SUBSCRIPTION: Запрос на подписку {} на пользователя с id {} успешно создан", subscriptionRequestDto, requesterId);
        return subscriptionRequestDto;
    }

    @PatchMapping("/{ownerId}/request/{requesterId}")
    public SubscriptionRequestDto updateFollowRequest(@PathVariable Long ownerId, @PathVariable Long requesterId,
                                                                 @RequestBody @Valid UpdateRequestStatus status) {

        log.info("SUBSCRIPTION: Получен запрос на изменение статуса {} подписки пользователем с id {} от пользователя с id {}", status, ownerId, requesterId);
        SubscriptionUpdateParam subscriptionUpdateParam = new SubscriptionUpdateParam();
        subscriptionUpdateParam.setOwnerId(ownerId);
        subscriptionUpdateParam.setRequesterId(requesterId);
        subscriptionUpdateParam.setStatus(status.getStatus());

        SubscriptionRequest subscriptionRequest = subscriptionService.updateFollow(subscriptionUpdateParam);
        SubscriptionRequestDto subscriptionRequestDto = SubscriptionMapper.toSubscriptionRequestDtoFromSubscriptionRequest(subscriptionRequest);
        log.info("SUBSCRIPTION: Запрос на изменение подписки от пользователя с id {} успешно изменен на {}", requesterId, status);
        return subscriptionRequestDto;
    }

    @GetMapping("/{followerId}/following/{followingId}")
    public List<EventFullDto> getEventsByFollowedId(@PathVariable Long followerId, @PathVariable Long followingId) {

        log.info("SUBSCRIPTION: Получен запрос на получение событий, созданным пользователем {}, на которого подписан пользователь {}", followingId, followerId);
        SubscriptionParam subscriptionParam = new SubscriptionParam();
        subscriptionParam.setFollowerId(followerId);
        subscriptionParam.setFollowingId(followingId);

        List<Event> eventList = eventService.getEventsByFollowedId(subscriptionParam);
        List<EventFullDto> eventShortDtoList = EventMapper.toListEventFullDtoFromListEvent(eventList);
        log.info("SUBSCRIPTION: Получен список событий {}, созданным пользователем {}, на которого подписан пользователь {}", eventShortDtoList, followingId, followerId);
        return eventShortDtoList;
    }

    @GetMapping("/{userId}/followers")
    public List<UserShortDto> getFollowers(@PathVariable Long userId) {

        log.info("SUBSCRIPTION: Получен запрос на получение подписчиков пользователя {}", userId);
        SubscriptionParam subscriptionParam = new SubscriptionParam();
        subscriptionParam.setFollowingId(userId);

        List<User> userList = subscriptionService.getFollowers(subscriptionParam);
        List<UserShortDto> userShortDto = UserMapper.fromUserListToListUserShortDto(userList);
        log.info("SUBSCRIPTION: Получен список подписчиков {} пользователя с id {}", userShortDto, userId);
        return userShortDto;
    }

    @GetMapping("/{userId}/following")
    public List<UserShortDto> getFollowings(@PathVariable Long userId) {

        log.info("SUBSCRIPTION: Получен запрос на получение подписок пользователя {}", userId);
        SubscriptionParam subscriptionParam = new SubscriptionParam();
        subscriptionParam.setFollowerId(userId);

        List<User> userList = subscriptionService.getSubscriptions(subscriptionParam);
        List<UserShortDto> userShortDto = UserMapper.fromUserListToListUserShortDto(userList);
        log.info("SUBSCRIPTION: Получен список подписок {} пользователя с id {}", userShortDto, userId);
        return userShortDto;
    }

    @PatchMapping("/{userId}/followers/{followingId}/cancel")
    public SubscriptionDto cancelSubscription(@PathVariable Long userId, @PathVariable Long followingId) {

        log.info("SUBSCRIPTION: Получен запрос от пользователя с id {} на отмену подписки на пользователя с id {}", userId, followingId);
        SubscriptionParam subscriptionParam = new SubscriptionParam();
        subscriptionParam.setFollowingId(followingId);
        subscriptionParam.setFollowerId(userId);

        Subscription subscription = subscriptionService.cancelSubscription(subscriptionParam);
        SubscriptionDto subscriptionDto = SubscriptionMapper.toSubscriptionDtoFromSubscription(subscription);
        log.info("SUBSCRIPTION: Подписка на пользователя с id {} успешно отменена, теперь ваши подписчики и подписки: {}", followingId, subscriptionDto);
        return subscriptionDto;
    }

    @PatchMapping("/{userId}/followings/{followerId}/cancel")
    public SubscriptionDto cancelFollower(@PathVariable Long userId, @PathVariable Long followerId) {

        log.info("SUBSCRIPTION: Получен запрос от пользователя с id {} на отмену подписки от пользователя с id {}", userId, followerId);
        SubscriptionParam subscriptionParam = new SubscriptionParam();
        subscriptionParam.setFollowingId(userId);
        subscriptionParam.setFollowerId(followerId);

        Subscription subscription = subscriptionService.cancelFollower(subscriptionParam);
        SubscriptionDto subscriptionDto = SubscriptionMapper.toSubscriptionDtoFromSubscription(subscription);
        log.info("SUBSCRIPTION: Подписка от пользователя с id {} успешно отменена, теперь ваши подписчики и подписки: {}", followerId, subscriptionDto);
        return subscriptionDto;
    }






}
