package ewm.event;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublicEventParam {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    private String rangeStart;

    private String rangeEnd;

    private boolean onlyAvailable;

    private String sort;

    private int from;

    private int size;

    private Long eventId;

    private String ip;

    private String uri;

}
