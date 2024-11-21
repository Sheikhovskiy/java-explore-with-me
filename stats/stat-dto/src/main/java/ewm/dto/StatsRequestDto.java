package ewm.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StatsRequestDto {

    @NotBlank
    private String start;

    @NotBlank
    private String end;

    private String[] uris;

    private boolean unique;
}
