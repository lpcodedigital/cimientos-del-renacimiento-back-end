package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppMobileObraDetailDTO {
    private Long id;
    private String name;
    private String municipality;
    private String locality;
    private String agency;
    private String investment;
    private Integer progress;
    private String description;
    private Double latitude;
    private Double longitude;
    private String status;
    private List<String> galeryUrls;
}
