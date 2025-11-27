package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {

    private Long idPermission;
    private String name;
    private String description;
}
