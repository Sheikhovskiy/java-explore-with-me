package ewm.subscription;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionUpdateParam {

    private Long ownerId;

    private Long requesterId;

    private String status;


}
