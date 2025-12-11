package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Tiene un único propósito: permitir que tu GlobalExceptionHandler capture todas las excepciones de negocio bajo un mismo paraguas.
public abstract class ApiException extends RuntimeException {


    public ApiException(String message) {
        super(message);
    }

}
