package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Resend2FADTO {
    @NotBlank(message = "El correo électronico es obligatorio")
    @Email
    private String email;
}
