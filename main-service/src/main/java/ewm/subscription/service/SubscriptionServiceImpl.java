package ewm.subscription.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.exception.ConditionsNotRespected;
import ewm.exception.NotFoundException;
import ewm.subscription.SubscriptionParam;
import ewm.subscription.SubscriptionRequestParam;
import ewm.subscription.SubscriptionStatus;
import ewm.subscription.SubscriptionUpdateParam;
import ewm.subscription.model.Subscription;
import ewm.subscription.model.SubscriptionRequest;
import ewm.subscription.repository.SubscriptionRepository;
import ewm.subscription.repository.SubscriptionRequestRepository;
import ewm.user.QUser;
import ewm.user.User;
import ewm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final SubscriptionRequestRepository subscriptionRequestRepository;

    private final UserRepository userRepository;

    private static final String NOT_EXISTING_USER = "Пользователь с id %s не найден";

    @Override
    public SubscriptionRequest createFollow(SubscriptionRequestParam subscriptionRequestParam) {
        Long requesterId = subscriptionRequestParam.getFollowerId();
        Long userId = subscriptionRequestParam.getFollowingId();

        User requester = checkIfUserExists(requesterId);
        User userToFollow = checkIfUserExists(userId);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setRequester(requester);
        subscriptionRequest.setUserToFollow(userToFollow);
        subscriptionRequest.setCreatedOn(LocalDateTime.now());
        subscriptionRequest.setStatus(SubscriptionStatus.PENDING);

        boolean requestAlreadyExist = subscriptionRequestRepository.checkIfExistsByRequesterIdAndUserId(requesterId, userId);

        if (requestAlreadyExist) {
            throw new ConditionsNotRespected(String.format("Вы уже отправили запрос на подписку на пользователя с id %s", userId));
        }

        return subscriptionRequestRepository.save(subscriptionRequest);
    }

    @Override
    public SubscriptionRequest updateFollow(SubscriptionUpdateParam subscriptionUpdateParam) {

        Long userToFollowId = subscriptionUpdateParam.getOwnerId();
        Long requesterId = subscriptionUpdateParam.getRequesterId();
        SubscriptionStatus status = SubscriptionStatus.from(subscriptionUpdateParam.getStatus());

        User owner = checkIfUserExists(userToFollowId);
        User requester = checkIfUserExists(requesterId);

        SubscriptionRequest subscriptionRequest = subscriptionRequestRepository.findByRequesterIdAndUserId(requesterId, userToFollowId);

        if (subscriptionRequest == null) {
            throw new ConditionsNotRespected("Такого запроса на подписку не существует");
        }

        if (status.equals(SubscriptionStatus.APPROVE_REQUEST)) {
            Subscription subscriptionOwner = subscriptionRepository.findByOwnerId(userToFollowId);
            List<Long> followers = subscriptionOwner.getFollowers() != null ? subscriptionOwner.getFollowers() : new ArrayList<>();
            followers.add(requesterId);
            subscriptionOwner.setFollowers(new ArrayList<>(followers)); // Обновляем коллекцию
            subscriptionRepository.save(subscriptionOwner);

            Subscription subscriptionRequester = subscriptionRepository.findByOwnerId(requesterId);
            List<Long> subscriptions = subscriptionRequester.getSubscriptions() != null ? subscriptionRequester.getSubscriptions() : new ArrayList<>();
            subscriptions.add(userToFollowId); // Исправлено: добавляем userToFollowId
            subscriptionRequester.setSubscriptions(new ArrayList<>(subscriptions)); // Обновляем коллекцию
            subscriptionRepository.save(subscriptionRequester);

            subscriptionRequest.setStatus(SubscriptionStatus.APPROVED);

        } else if (status.equals(SubscriptionStatus.REJECT_REQUEST)) {
            subscriptionRequest.setStatus(SubscriptionStatus.REJECTED);
        } else {
            throw new ConditionsNotRespected(String.format("Статуса с таким названием %s не существует!", status));
        }

        return subscriptionRequestRepository.save(subscriptionRequest);
    }

    @Override
    public List<User> getFollowers(SubscriptionParam subscriptionParam) {
        Long userId = subscriptionParam.getFollowingId();

        User user = checkIfUserExists(userId);

        Subscription subscription = subscriptionRepository.findByOwnerId(userId);
        List<Long> followersIds  = subscription.getFollowers();

        QUser qUser = QUser.user;

        BooleanExpression predicate = qUser.id.in(followersIds);

        Iterable<User> userIterable = userRepository.findAll(predicate);
        List<User> userList = new ArrayList<>();
        userIterable.forEach(userList::add);

        return userList;
    }

    @Override
    public List<User> getSubscriptions(SubscriptionParam subscriptionParam) {
        Long userId = subscriptionParam.getFollowerId();

        User user = checkIfUserExists(userId);

        Subscription subscription = subscriptionRepository.findByOwnerId(userId);
        List<Long> subscriptionsIds  = subscription.getSubscriptions();

        QUser qUser = QUser.user;

        BooleanExpression predicate = qUser.id.in(subscriptionsIds);

        Iterable<User> userIterable = userRepository.findAll(predicate);
        List<User> userList = new ArrayList<>();
        userIterable.forEach(userList::add);

        return userList;
    }

    @Override
    public Subscription cancelSubscription(SubscriptionParam subscriptionParam) {

        Long userId = subscriptionParam.getFollowerId();
        Long followingId = subscriptionParam.getFollowingId();

        User user = checkIfUserExists(userId);
        User following = checkIfUserExists(followingId);

        Subscription subscriptionOwner = subscriptionRepository.findByOwnerId(userId);
        List<Long> subscriptions = subscriptionOwner.getSubscriptions();
        if (subscriptions.contains(followingId)) {
            List<Long> newSubscriptions = new ArrayList<>(subscriptions);
            newSubscriptions.remove(followingId);
            subscriptionOwner.setSubscriptions(newSubscriptions);
            subscriptionRepository.save(subscriptionOwner);
        } else {
            throw new ConditionsNotRespected(String.format("Вы не подписаны на пользователя с id %s", followingId));
        }

        Subscription subscriptionFollowing = subscriptionRepository.findByOwnerId(followingId);
        List<Long> followers = subscriptionFollowing.getFollowers();
        List<Long> newFollowers = new ArrayList<>(followers);
        newFollowers.remove(userId);
        subscriptionFollowing.setFollowers(newFollowers);
        subscriptionRepository.save(subscriptionFollowing);

        return subscriptionRepository.save(subscriptionOwner);
    }

    @Override
    public Subscription cancelFollower(SubscriptionParam subscriptionParam) {
        Long userId = subscriptionParam.getFollowingId();
        Long followerId = subscriptionParam.getFollowerId();

        User user = checkIfUserExists(userId);
        User follower = checkIfUserExists(followerId);

        Subscription subscriptionOwner = subscriptionRepository.findByOwnerId(userId);
        List<Long> followers = subscriptionOwner.getFollowers();

        if (followers.contains(followerId)) {
            List<Long> newFollowers = new ArrayList<>(followers);
            newFollowers.remove(followerId);
            subscriptionOwner.setFollowers(newFollowers);
            subscriptionRepository.save(subscriptionOwner);

        } else {
            throw new ConditionsNotRespected(String.format("На вас не подписан пользователь с id %s", followerId));
        }

        Subscription subscriptionFollower = subscriptionRepository.findByOwnerId(followerId);
        List<Long> subscriptions = subscriptionFollower.getFollowers();
        List<Long> newSubscriptions = new ArrayList<>(subscriptions);
        newSubscriptions.remove(userId);
        subscriptionFollower.setSubscriptions(newSubscriptions);
        subscriptionRepository.save(subscriptionFollower);

        return subscriptionRepository.save(subscriptionOwner);
    }

    private User checkIfUserExists(Long userId) {
        Optional<User> userOpt = userRepository.getUserById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_USER, userId));
        }

        return userOpt.get();
    }

}
