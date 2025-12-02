package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;

    private Date expiresAt;

    private AuthBasicUserResponseDTO user;

}
