package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo électronico es obligatorio") 
    private String email;

    @NotBlank(message = "El código de verificación es obligatorio")
    private String code;

    @NotBlank(message = "La contraseña es obligatoria")
    private String newPassword;
}
