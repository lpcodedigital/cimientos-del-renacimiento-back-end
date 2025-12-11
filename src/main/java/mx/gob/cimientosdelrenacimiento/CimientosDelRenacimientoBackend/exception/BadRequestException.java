package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Cuando los datos enviados no son válidos o rompen reglas.
// HTTP 400 Bad Request
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message);
    }

}
