package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Cuando el usuario no tiene un JWT válido (aunque Spring maneja la mayoría de casos).
// HTTP 401 Unauthorized
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(message);
    }

}
