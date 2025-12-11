package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Cuando hay conflictos: email ya existe, rol duplicado, intento de reactivar algo eliminado, etc.
// HTTP 409 Conflict Exception
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(message);
    }

}
