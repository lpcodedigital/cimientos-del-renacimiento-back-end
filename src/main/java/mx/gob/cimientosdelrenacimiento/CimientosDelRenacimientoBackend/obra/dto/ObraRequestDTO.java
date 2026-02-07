package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.EstadoObraEnum;

@Data
public class ObraRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El municipio es obligatorio")
    private String municipality;

    @NotBlank(message = "La ejecutora es obligatoria")
    private String agency;

    @PositiveOrZero(message = "La inversión no puede ser negativa")
    private BigDecimal investment;

    @Min(value = 0, message = "El avance debe ser al menos 0") 
    @Max(value = 100, message = "El avance no puede ser mayor a 100")
    private Integer progress;

    private String description;

    @NotNull(message = "La latitud es obligatoria")
    @Min(value = -90, message = "Latitud inválida")
    @Max(value = 90, message = "Latitud inválida")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @Min(value = -180, message = "Longitud inválida")
    @Max(value = 180, message = "Longitud inválida")    
    private Double longitude;

    @NotNull(message = "El estado de la obra es obligatorio")
    private EstadoObraEnum status;

    private List<Long> keepImageIds;

}
