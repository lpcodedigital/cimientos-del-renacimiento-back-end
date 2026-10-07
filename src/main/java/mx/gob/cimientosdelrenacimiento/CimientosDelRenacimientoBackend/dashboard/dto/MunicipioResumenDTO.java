package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class MunicipioResumenDTO {
    private String municipio;
    private Long totalObras = 0L;
    private Long totalCursos = 0L;
    private BigDecimal totalInversion = BigDecimal.ZERO;
    private Long obrasFinalizadas = 0L;
    private Long obrasEnProceso = 0L;
    
    // Coordenadas representativas para el pin en el mapa
    private Double latitude;
    private Double longitude;
}
