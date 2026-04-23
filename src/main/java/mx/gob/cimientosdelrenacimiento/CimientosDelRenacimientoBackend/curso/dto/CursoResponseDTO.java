package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CursoResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate courseDate;
    private String municipalityName;
    private Long municipalityId;
    
    private CursoImageDTO coverImage;
    private List<CursoImageDTO> images;

    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
}
