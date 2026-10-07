package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.projections;

public interface CursoStatsProjectionForApp {
    String getMunicipality();
    Long getTotalCursos();
    Double getLatitude();
    Double getLongitude();
}
