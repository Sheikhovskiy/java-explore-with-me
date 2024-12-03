package ewm.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CompilationUpdatedDto {

    private List<Long> events;

    private Long id;

    private boolean pinned;

    private String title;

}
