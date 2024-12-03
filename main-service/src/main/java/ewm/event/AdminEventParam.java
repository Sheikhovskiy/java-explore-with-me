package ewm.event;

import ewm.event.model.Event;
import ewm.user.dto.UpdateEventAdminRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminEventParam {

    private List<Long> users;

    private List<String> states;

    private List<Long> categories;

    private String rangeStart;

    private String rangeEnd;

    private int from;

    private int size;

    private Long eventId;

    private Event event;

    private Long category;

    private UpdateEventAdminRequest updateEventAdminRequest;
}
