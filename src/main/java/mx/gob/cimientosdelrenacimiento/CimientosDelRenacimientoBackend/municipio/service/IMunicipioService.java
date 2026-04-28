package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.service;

import java.util.List;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.dto.MunicipioResponseDTO;

public interface IMunicipioService {

    List<MunicipioResponseDTO> findAll();
}
