package ewm.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class ViewStatsDto {

    private Long id;

    private String app;

    private String uri;

    private LocalDateTime timestamp;

    private long hits;
}
