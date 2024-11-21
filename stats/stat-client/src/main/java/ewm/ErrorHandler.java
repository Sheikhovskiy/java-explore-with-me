//package ewm;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//
//@Slf4j
//@RestControllerAdvice
//public class ErrorHandler {
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiError handleException(final Exception e, HttpStatus status) {
//        log.info("500 {}", e.getMessage());
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace();
//        String stackTrace = sw.toString();
//        return new ApiError(status, "Error ...", e.getMessage(), stackTrace);
//    }
//
//}
