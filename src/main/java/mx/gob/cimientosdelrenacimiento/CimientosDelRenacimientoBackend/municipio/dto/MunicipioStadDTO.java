package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MunicipioStadDTO {

    private String nombre;
    private Long totalObras;
}
