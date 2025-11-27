package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthBasicUserResponseDTO {

    private Long idUser;

    private String name;

    private String email;

    private Boolean active;

    private String role;

}
