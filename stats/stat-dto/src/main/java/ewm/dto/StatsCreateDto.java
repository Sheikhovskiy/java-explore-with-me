package ewm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatsCreateDto {

    private String start;

    private String end;

    private List<String> uris;

    private boolean unique;

}
