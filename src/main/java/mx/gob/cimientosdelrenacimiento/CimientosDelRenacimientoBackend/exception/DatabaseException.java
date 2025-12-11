package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Para errores de constraint, timeouts, integridad.
// HTTP 500 Internal Server Error o 509 
public class DatabaseException  extends ApiException {

    public DatabaseException(String message) {
        super(message);
    }

}
