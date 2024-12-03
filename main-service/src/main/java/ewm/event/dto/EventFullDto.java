package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.user.dto.UserShortDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    private boolean paid;

    private Long participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private String state;

    private String title;

    private Long views;


}
