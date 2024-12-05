package ewm.subscription.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubscriptionDto {

    private Long ownerId;

    private List<Long> followers;

    private List<Long> subscriptions;

}
