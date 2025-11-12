package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiException {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiException(HttpStatus status, String message, String path, String formattedTime) {
        this.timestamp = formattedTime;
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

}
