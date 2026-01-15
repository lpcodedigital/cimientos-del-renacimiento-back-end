package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ObraResponseDTO {
    private Long id;
    private String name;
    private String municipality;
    private String agency;
    private BigDecimal investment;
    private Integer progress;
    private String description;
    private Double latitude;
    private Double longitude;
    private String status;
    private List<ObraImageDTO> images;
    private LocalDateTime createdAt;
    private String createdBy;

}
