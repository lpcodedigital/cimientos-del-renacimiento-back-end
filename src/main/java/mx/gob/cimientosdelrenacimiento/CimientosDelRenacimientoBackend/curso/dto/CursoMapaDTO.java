package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto;

import lombok.Data;

@Data
public class CursoMapaDTO {

    private Long id;
    private String title;
    private Double latitude;
    private Double longitude;
    private String municipalityName;

}
