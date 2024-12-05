package ewm.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateRequestStatus {

    @NotBlank
    private String status;

}
