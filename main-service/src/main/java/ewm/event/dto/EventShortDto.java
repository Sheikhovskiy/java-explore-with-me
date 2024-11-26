package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.user.dto.UserShortDto;

public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    //  формате "yyyy-MM-dd HH:mm:ss"
    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private Long views;


}
