package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections;

import java.time.LocalDateTime;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.EstadoObraEnum;

public interface ObraPaginationProjection {
    Long getId();
    String getName();
    String getMunicipality();
    String getDescription();
    EstadoObraEnum getStatus();
    Integer getProgress();
    LocalDateTime getCreatedAt();
}
