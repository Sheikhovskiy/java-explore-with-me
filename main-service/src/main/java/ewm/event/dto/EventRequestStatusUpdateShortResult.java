package ewm.event.dto;

import ewm.request.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateShortResult {

    private List<Request> confirmedRequests;

    private List<Request> rejectedRequests;
}
