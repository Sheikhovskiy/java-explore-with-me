package ewm.admin.dto;

import ewm.event.model.Location;
import jakarta.validation.constraints.Size;

public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    // "yyyy-MM-dd HH:mm:ss"
    private String eventDate;

    private Location location;

    private boolean paid;

    private Long participantLimit;

    private boolean requestModeration;

    private String stateAction;

    private String title;

}
