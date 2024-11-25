package ewm.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class HitCreateDto {

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @NotBlank
    private String timestamp;

}
