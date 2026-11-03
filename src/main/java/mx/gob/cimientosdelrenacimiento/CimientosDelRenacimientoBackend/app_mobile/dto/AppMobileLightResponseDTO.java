package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.EstadoObraEnum;

public record AppMobileLightResponseDTO(
    String tipo,
    Long id,
    String name,
    String description,
    Double latitude,
    Double longitude,
    String municipality,
    String locality,
    String agency,
    BigDecimal investment,
    EstadoObraEnum status,
    Integer progress,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {}
