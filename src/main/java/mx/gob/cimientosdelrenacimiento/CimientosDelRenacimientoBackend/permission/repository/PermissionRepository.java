package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.model.PermissionModel;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {

}
