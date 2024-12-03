package ewm.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminUserParam {

    private List<Long> ids;

    private int from;

    private int size;

    private Long userId;

}
