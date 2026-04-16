package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Verify2FARequestDTO {

    @NotBlank(message = "El correo électronico es obligatorio")
    @Email
    private String email;

    @NotBlank(message = "El código de verificación es obligatorio")
    private String code;
}
