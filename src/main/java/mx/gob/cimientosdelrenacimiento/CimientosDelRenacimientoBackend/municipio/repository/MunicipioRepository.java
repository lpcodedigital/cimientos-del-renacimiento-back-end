package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model.MunicipioModel;

public interface MunicipioRepository extends JpaRepository<MunicipioModel, Long> {

    Optional<MunicipioModel> findByName(String name);

    List<MunicipioModel> findAllByOrderByNameAsc();

}
