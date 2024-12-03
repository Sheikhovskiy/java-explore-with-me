package ewm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsDto {

    private String app;

    private String uri;

    private Long hits;
}
