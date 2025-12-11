package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

// Cuando la validación de datos falla. unificar errores de @Valid.
// HTTP 400 Bad Request
public class ValidationException extends ApiException {
 
     public ValidationException(String message) {
         super(message);
     }

}
