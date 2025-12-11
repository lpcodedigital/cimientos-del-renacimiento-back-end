package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;

public interface UserRespository extends JpaRepository<UserModel, Long> {

        Optional<UserModel> findByEmail(String email);

        // Verificar si existe un usuario con el email dado, sin importar su estado activo/inactivo excluyendo los eliminados por soft delete
        @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
        Optional<UserModel> findAnyByEmail(@Param("email") String email);


    }
