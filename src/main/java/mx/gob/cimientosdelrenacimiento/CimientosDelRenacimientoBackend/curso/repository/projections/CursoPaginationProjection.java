package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.projections;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CursoPaginationProjection {
    Long getId();
    String getTitle();
    String getDescription();
    LocalDate getCourseDate();

    String getMunicipioName();
    Long getMunicipioId();

    // Aquí traemos solo la URL de la portada de forma eficiente
    // Gracias al JOIN que haremos en el Repository
    String getCoverImageUrl(); 

    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
