package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasicUserDTO {

    private Long idUser;

    private String name;

    private String email;

    private Boolean active;

    private String role;

}
