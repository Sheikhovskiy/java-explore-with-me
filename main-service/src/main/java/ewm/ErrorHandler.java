package ewm;

import ewm.exception.ApiError;
import ewm.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NotFoundException e) {
        return generalHandle(e, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handle(final ValidationException e) {
//        return generalHandle("Validation: " + e.getMessage());
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Throwable e) {
        log.warn("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();


//        return new ApiError(e.getMessage());
        return new ApiError(
                "Неизвестная ошибка",
                new ArrayList<>(Integer.parseInt(e.getMessage())),
                e.getMessage(),
                stackTrace,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now()
        );
    }


    private ApiError generalHandle(Exception error, HttpStatus httpStatus) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String stackTrace = sw.toString();

        return new ApiError(
                String.format("Ошибка {}", error),
                new ArrayList<>(Integer.parseInt(error.getMessage())),
                error.getMessage(),
                stackTrace,
                httpStatus.name(),
                LocalDateTime.now()
        );
    }

}
