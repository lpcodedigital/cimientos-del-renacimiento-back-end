package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CursoRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
    private Long municipalityId;
    private LocalDate courseDate;
    private List<Long> keepImageIds;
    @NotNull(message = "La imagen de portada es obligatoria")
    private Long currentCoverImageId;
}
