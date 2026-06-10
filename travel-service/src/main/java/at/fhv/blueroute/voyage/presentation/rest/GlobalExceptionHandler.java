package at.fhv.blueroute.voyage.presentation.rest;

import at.fhv.blueroute.voyage.application.exception.VoyageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VoyageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleVoyageException(VoyageException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "VOYAGE_ERROR",
                "message", ex.getMessage()
        );
    }
}
