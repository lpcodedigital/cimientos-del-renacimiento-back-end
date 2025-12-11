package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Usuario autenticado, pero sin permisos.
// HTTP 403 Forbidden
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(message);
    }

}
