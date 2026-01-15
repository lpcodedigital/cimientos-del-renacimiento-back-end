package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto;

import lombok.Data;

@Data
public class ObraMapaDTO {

    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String municipality;
}
