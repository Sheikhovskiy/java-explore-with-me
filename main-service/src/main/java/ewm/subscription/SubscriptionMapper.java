package ewm.subscription;

import ewm.subscription.dto.SubscriptionDto;
import ewm.subscription.dto.SubscriptionRequestDto;
import ewm.subscription.model.Subscription;
import ewm.subscription.model.SubscriptionRequest;
import ewm.user.UserMapper;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@UtilityClass
public class SubscriptionMapper {

    public SubscriptionRequestDto toSubscriptionRequestDtoFromSubscriptionRequest(SubscriptionRequest subscriptionRequest) {

        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto();
        subscriptionRequestDto.setId(subscriptionRequest.getId());
        subscriptionRequestDto.setUserShortDto(UserMapper.fromUserToUserShortDto(subscriptionRequest.getUserToFollow()));
        subscriptionRequestDto.setCreatedOn(fromLocalDateTimeToString(subscriptionRequest.getCreatedOn()));
        subscriptionRequestDto.setStatus(String.valueOf(subscriptionRequest.getStatus()));

        return subscriptionRequestDto;
    }



    public static SubscriptionDto toSubscriptionDtoFromSubscription(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setOwnerId(subscription.getOwner().getId());
        subscriptionDto.setFollowers(subscription.getFollowers());
        subscriptionDto.setSubscriptions(subscription.getSubscriptions());

        return subscriptionDto;
    }


    private String fromLocalDateTimeToString(LocalDateTime localDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        LocalDateTime dateTime = LocalDateTime.from(localDateTime);
        return dateTime.format(formatter);
    }


}