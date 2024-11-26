package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.event.model.Location;
import ewm.user.dto.UserShortDto;

public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    // в формате "yyyy-MM-dd HH:mm:ss"
    private String createdOn;

    private String description;

    // в формате "yyyy-MM-dd HH:mm:ss"
    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private Long participantLimit;

    // в формате "yyyy-MM-dd HH:mm:ss"
    private String publishedOn;

    private boolean requestModeration;

    private String state;

    private String title;

    private Long views;


}
