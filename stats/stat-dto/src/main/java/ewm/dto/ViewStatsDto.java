package ewm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewStatsDto {

    private String app;

    private String uri;

    private long hits;
}
