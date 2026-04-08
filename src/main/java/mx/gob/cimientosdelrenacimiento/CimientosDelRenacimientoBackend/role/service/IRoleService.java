package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.service;

import java.util.List;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.dto.RoleDTO;

public interface IRoleService {

    List<RoleDTO> findAll();
}
