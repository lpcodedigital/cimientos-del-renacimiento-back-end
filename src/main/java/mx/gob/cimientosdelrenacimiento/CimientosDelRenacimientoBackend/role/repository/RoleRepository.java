package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {

    Optional<RoleModel> findByName(String name);
}
