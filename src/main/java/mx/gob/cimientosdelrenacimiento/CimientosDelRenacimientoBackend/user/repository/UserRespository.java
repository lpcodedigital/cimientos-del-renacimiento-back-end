package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;

public interface UserRespository extends JpaRepository<UserModel, Long> {

        Optional<UserModel> findByEmail(String email);

    }
