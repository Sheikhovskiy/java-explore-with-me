package ewm.subscription.service;

import ewm.subscription.SubscriptionParam;
import ewm.subscription.SubscriptionRequestParam;
import ewm.subscription.SubscriptionUpdateParam;
import ewm.subscription.model.Subscription;
import ewm.subscription.model.SubscriptionRequest;
import ewm.user.User;

import java.util.List;

public interface SubscriptionService {

    SubscriptionRequest createFollow(SubscriptionRequestParam subscriptionRequestParam);

    SubscriptionRequest updateFollow(SubscriptionUpdateParam subscriptionUpdateParam);

    List<User> getFollowers(SubscriptionParam subscriptionParam);

    List<User> getSubscriptions(SubscriptionParam subscriptionParam);

    Subscription cancelSubscription(SubscriptionParam subscriptionParam);

    Subscription cancelFollower(SubscriptionParam subscriptionParam);
}
