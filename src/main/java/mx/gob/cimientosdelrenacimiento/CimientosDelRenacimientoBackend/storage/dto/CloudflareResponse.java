package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto;

import java.util.List;

public record CloudflareResponse(
    Result result,
    boolean success,
    List<Object> errors,
    List<Object> messages
) {
    public record Result(
        String id,
        String filename,
        String uploaded,
        boolean requireSignedURLs,
        List<String> variants
    ){}
}
