package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.mapper;

import org.springframework.stereotype.Component;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthBasicUserResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;

@Component
public class AuthMapper {

        public AuthBasicUserResponseDTO toAuthBasicUserDTO(UserModel user) {
        return AuthBasicUserResponseDTO.builder()
            .idUser(user.getIdUser())
            .name(user.getName())
            .email(user.getEmail())
            .active(user.getActive())
            .role(user.getRole().getName())
            .build();
    }

}
