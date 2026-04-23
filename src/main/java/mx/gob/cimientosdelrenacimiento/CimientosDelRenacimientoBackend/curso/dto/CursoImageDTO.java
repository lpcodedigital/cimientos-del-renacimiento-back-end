package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoImageDTO {
    private Long id;
    private String url;
    private String thumbUrl;
    private String mimeType;
    private Integer position;
}
