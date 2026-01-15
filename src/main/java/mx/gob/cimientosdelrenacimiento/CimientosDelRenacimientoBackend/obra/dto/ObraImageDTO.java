package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto;

import lombok.Data;

@Data
public class ObraImageDTO {
    private Long id;
    private String url;
    private String thumbUrl;
    private String mimeType;
    private String size;
    private Integer position;
}
