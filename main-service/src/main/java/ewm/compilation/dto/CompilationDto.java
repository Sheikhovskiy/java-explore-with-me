package ewm.compilation.dto;

import ewm.event.dto.EventShortDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CompilationDto {

    private List<EventShortDto> events;

    private Long id;

    private boolean pinned;

    private String title;

}
