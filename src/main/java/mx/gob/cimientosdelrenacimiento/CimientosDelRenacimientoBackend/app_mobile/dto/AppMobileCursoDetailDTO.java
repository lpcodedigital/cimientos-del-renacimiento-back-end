package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
@AllArgsConstructor 
public class AppMobileCursoDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String municipality;
    private String courseDate;
    private Double latitude;
    private Double longitude;
    private String coverImageUrl;
    private List<String> galeryUrls;
}
