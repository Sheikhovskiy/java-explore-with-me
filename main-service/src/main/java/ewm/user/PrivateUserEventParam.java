package ewm.user;


import ewm.event.dto.EventRequestStatusUpdateRequest;
import ewm.event.model.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateUserEventParam {

    private Long userId;

    private Long eventId;

    private int from;

    private int size;

    private Event event;

    private Long category;

    private EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;

}
