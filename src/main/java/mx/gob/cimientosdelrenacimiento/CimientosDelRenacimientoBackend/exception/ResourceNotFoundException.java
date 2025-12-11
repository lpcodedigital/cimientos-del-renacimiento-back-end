package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Representa “no existe”: usuarios, roles, obras, cursos, etc.
// HTTP 404 Not Found
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
