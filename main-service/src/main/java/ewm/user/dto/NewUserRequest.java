package ewm.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewUserRequest {

    @NotBlank
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

}
