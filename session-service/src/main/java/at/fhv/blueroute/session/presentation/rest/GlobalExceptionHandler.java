package at.fhv.blueroute.session.presentation.rest;

import at.fhv.blueroute.session.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            SessionNotFoundException.class,
            SessionPlayerNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(RuntimeException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler({
            PlayerAlreadyInSessionException.class,
            SessionFullException.class,
            PlayerAlreadyActiveException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(RuntimeException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 409,
                "error", "CONFLICT",
                "message", ex.getMessage()
        );
    }
}
