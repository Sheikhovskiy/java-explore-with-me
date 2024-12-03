package ewm.compilation.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    private String title;

}
