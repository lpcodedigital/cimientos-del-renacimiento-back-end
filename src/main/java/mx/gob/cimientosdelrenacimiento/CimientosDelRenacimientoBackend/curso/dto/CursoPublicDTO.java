package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CursoPublicDTO {
    private Long id;
    private String title;
    private String description;
    private String municipalityName;
    private LocalDate courseDate;
    private String coverImageUrl;
}
