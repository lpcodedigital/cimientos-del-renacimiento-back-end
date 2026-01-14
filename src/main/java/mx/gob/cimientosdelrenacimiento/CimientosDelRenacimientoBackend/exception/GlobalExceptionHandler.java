package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.DateFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private DateFormatter dateTimeFormater;

    // 404 - ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 409 - ConflictException
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 403 - ForbiddenException (AUTENTICADO pero sin permisos)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 400 - BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 401 - UnauthorizedException (NO autenticado o token inválido)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 400 - ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 500 - DatabaseException
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException ex, HttpServletRequest request) {
        ErrorResponse error  = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // 400 - MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions( MethodArgumentNotValidException ex, HttpServletRequest request){
        
        Map<String, String> errors = new HashMap<>();

        // Extraemos cada error de campo y su mensaje personalizado
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Construimos el cuerpo de la respuesta con los detalles de los errores
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", dateTimeFormater.formatDateTime(LocalDateTime.now()));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", "Los datos proporcionados no son válidos.");
        response.put("path", request.getRequestURI());
        response.put("errors", errors); // Detalles de los errores de validación

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Captura errores de formato JSON (como enviar letras en campos numéricos)
    // 400 - Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {

        String errorMessage = "Error en el formato del JSON: Verifique que los campos numéricos (investment, progress, latitude, longitude) no contengan texto.";

        // Si quieres ser más específico, puedes inspeccionar la causa:
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife = (com.fasterxml.jackson.databind.exc.InvalidFormatException) ex.getCause();
            errorMessage = "El campo '" + ife.getPath().get(0).getFieldName() + "' debe ser de tipo numérico.";
        }

        ErrorResponse error = new ErrorResponse(
                dateTimeFormater.formatDateTime(LocalDateTime.now()),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
