package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.DateFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private DateFormatter dateTimeFormater;
        // Manejo de errores genéricos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleException(Exception ex, HttpServletRequest request) {
        ApiException error = new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI(),
                dateTimeFormater.formatDateTime(LocalDateTime.now())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Ejemplo de manejo de validaciones (por ejemplo con @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ApiException error = new ApiException(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI(),
                dateTimeFormater.formatDateTime(LocalDateTime.now())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
