package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.projections;

import java.time.LocalDate;

public interface CursoPaginationProjection {
    Long getId();
    String getTitle();
    String getDescription();
    String getMunicipalityName();
    LocalDate getCourseDate();

    // Aquí traemos solo la URL de la portada de forma eficiente
    // Gracias al JOIN que haremos en el Repository
    String getCoverImageUrl(); 
}
