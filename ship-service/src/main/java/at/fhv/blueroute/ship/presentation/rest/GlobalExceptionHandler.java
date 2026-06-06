package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.exception.*;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
      String errors = ex.getBindingResult().getFieldErrors()
              .stream()
              .map(FieldError::getDefaultMessage)
              .collect(Collectors.joining(", "));
      return Map.of("message", errors);
  }

  @ExceptionHandler({
          ShipNotFoundException.class,
          PlayerNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFound(RuntimeException ex) {
    return Map.of("message", ex.getMessage());
  }

  @ExceptionHandler({
          InvalidFuelAmountException.class,
          FuelCapacityExceededException.class,
          InvalidRepairAmountException.class,
          RepairLimitExceededException.class,
          InsufficientBalanceException.class,
          ShipCurrentlyTravelingException.class,
          ShipOutOfStockException.class,
          IllegalArgumentException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleBadRequest(RuntimeException ex) {
    return Map.of("message", ex.getMessage());
  }
}