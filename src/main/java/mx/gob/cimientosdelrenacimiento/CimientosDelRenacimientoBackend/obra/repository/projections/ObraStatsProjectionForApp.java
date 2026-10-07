package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections;

import java.math.BigDecimal;

public interface ObraStatsProjectionForApp {
    String getMunicipality();
    Long getTotalObras();
    BigDecimal getTotalInversion();
    Long getFinalizadas();
    Long getEnProceso();
    Double getLatitude();
    Double getLongitude();
}
