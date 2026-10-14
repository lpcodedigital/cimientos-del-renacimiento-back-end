package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Oculta del JSON los campos que vengan nulos
public class AppMobileGlobalSearchCardDTO {

    private Long idOriginal;
    private String tipo; // "OBRA" o "CURSO"
    private String titulo;
    private String municipio;
    private String localidad; // Se omitirá automáticamente en los Cursos
    private String descripcionCorta;
    private String estatus; // Se omitirá automáticamente en los Cursos
    private Integer progreso; // Se omitirá automáticamente en los Cursos
    private String imagenUrl;

}
