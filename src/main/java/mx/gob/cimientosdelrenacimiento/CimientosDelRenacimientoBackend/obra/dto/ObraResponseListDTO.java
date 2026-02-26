package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ObraResponseListDTO {
    private Long id;
    private String name;
    private String municipality;
    private String description;
    private String status;
    private Integer progress;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
