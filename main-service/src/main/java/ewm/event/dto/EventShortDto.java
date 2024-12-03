package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.user.dto.UserShortDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private Long views;


}
