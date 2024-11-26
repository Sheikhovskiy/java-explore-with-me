package ewm.exception;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ApiError {

    private final String description;

    private final List<String> errorsList;

    private final String message;

    private final String reason;

    private final String httpStatus;

    private final LocalDateTime timestamp;

}
