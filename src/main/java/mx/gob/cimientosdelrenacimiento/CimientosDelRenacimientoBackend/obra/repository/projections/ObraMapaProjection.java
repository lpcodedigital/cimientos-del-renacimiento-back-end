package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections;

public interface ObraMapaProjection {

    Long getId();
    String getName();
    Double getLatitude();
    Double getLongitude();
    String getMunicipality();
}
