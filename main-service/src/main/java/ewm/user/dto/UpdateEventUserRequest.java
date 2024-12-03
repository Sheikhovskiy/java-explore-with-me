package ewm.user.dto;

import ewm.event.model.Location;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private boolean paid;

    @Positive
    private Long participantLimit;

    private boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;

}
