package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto;

public record ImageStorageResponse(
    String url,
    String providerId,
    String thumbUrl,
    String mimeType,
    String size
) {}
