package com.home.video.exception;

import com.home.common.exception.HomeException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j(topic = "VIDEO")
public class GlobalExceptionHandler {

  @ExceptionHandler(HomeException.class)
  public ResponseEntity<Object> handleHomeException(
      final HomeException ex, final WebRequest request) {
    logException(ex, request);
    return ResponseEntity.badRequest().body(createExceptionBody("Bad Request Error"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(final Exception ex, final WebRequest request) {
    logException(ex, request);
    return ResponseEntity.internalServerError().body(createExceptionBody("Internal Server Error"));
  }

  private void logException(final Exception ex, final WebRequest request) {
    try {
      log.error("GlobalExceptionHandler::handleAllExceptions, exception: ", ex);
      log.error("GlobalExceptionHandler::handleAllExceptions, request: {}", request);
    } catch (Exception ignored) {
    }
  }

  private Map<String, Object> createExceptionBody(String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("error", message);
    return body;
  }
}
