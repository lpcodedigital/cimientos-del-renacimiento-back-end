package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.service;

import java.util.List;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto.DashboardStatsDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto.MunicipioResumenDTO;

public interface IDashboardService {

    DashboardStatsDTO getStats();

    List<MunicipioResumenDTO> getResumenGlobalPorMunicipioApp();
}
