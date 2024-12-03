package ewm;

import ewm.exception.ApiError;
import ewm.exception.ConditionsNotRespected;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format(
                        "Field '%s': %s",
                        error.getField(),
                        error.getDefaultMessage()))
                .toList();

        return new ApiError(
                "Ошибка валидации",
                errors,
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MissingServletRequestParameterException e) {
        log.warn("400 Параметр запроса отсутствует в запросе: {}", e.getMessage());
        return new ApiError(
                "Параметр запроса отсутствует в запросе",
                Collections.singletonList(e.getParameterName() + " Должен присутствовать в запросе"),
                e.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final BadRequestException e) {
        log.warn("400 Некорректный запрос: {}", e.getMessage());
        return new ApiError(
                "Запрос неверный",
                Collections.singletonList(e + " Запрос неверный"),
                e.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ValidationException e) {
        log.warn("400 Некорректное тело запроса: {}", e.getMessage());
        return new ApiError(
                "Запрос не соответствует ожидаемым параметрам переменных",
                Collections.singletonList(e + " Тело Запроса неверное"),
                e.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final ConditionsNotRespected e) {
        return generalHandle(e, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Throwable e) {
        log.warn("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();


        return new ApiError(
                "Неизвестная ошибка",
                Collections.singletonList(e.getMessage()),
                e.getMessage(),
//                stackTrace,
                "",
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
                String.format("Ошибка %s", error.getClass().getSimpleName()),
                Collections.singletonList(error.getMessage()),
                error.getMessage(),
//                stackTrace,
                "",
                httpStatus.name(),
                LocalDateTime.now()
        );
    }

}
