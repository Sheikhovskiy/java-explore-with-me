package ewm.subscription.dto;

import ewm.user.dto.UserShortDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class SubscriptionRequestDto {

    private Long id;

    private UserShortDto userShortDto;

    private String createdOn;

    private String status;

}
