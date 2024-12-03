package ewm.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateUserRequestParam {

    private Long userId;

    private Long eventId;

    private Long requestId;
}
