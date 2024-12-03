package ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class NewCompilationDto {

    private List<Long> events;

    @Builder.Default
    private boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

}
