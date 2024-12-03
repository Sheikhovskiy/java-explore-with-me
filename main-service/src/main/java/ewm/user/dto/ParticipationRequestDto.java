package ewm.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParticipationRequestDto {

    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private String status;
}
