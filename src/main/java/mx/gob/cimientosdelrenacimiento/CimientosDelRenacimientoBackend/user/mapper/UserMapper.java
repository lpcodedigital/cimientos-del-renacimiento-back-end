package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto.UserDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto.BasicUserDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto.RoleDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto.UserRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.DateFormatter;

@Component
public class UserMapper {

    @Autowired
    private DateFormatter dateTimeFormater;

    public UserModel toUserModel(UserRequestDTO dto, RoleModel role) {
        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setMiddleName(dto.getMiddleName());
        user.setFirstLastName(dto.getFirstLastName());
        user.setSecondLastName(dto.getSecondLastName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setActive(dto.getActive());
        user.setIsFirstLogin(dto.getIsFirstLogin());
        user.setTwoFactorEnabled(dto.getTwoFactorEnabled());
        user.setTwoFactorSecret(dto.getTwoFactorSecret());
        user.setVerificationCode(dto.getVerificationCode());
        user.setRole(role);
        return user;
    }

    public void updateUserFromDTO(UserRequestDTO dto, UserModel user, RoleModel role) {
        user.setName(dto.getName());
        user.setMiddleName(dto.getMiddleName());
        user.setFirstLastName(dto.getFirstLastName());
        user.setSecondLastName(dto.getSecondLastName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setActive(dto.getActive());
        if (role != null ) user.setRole(role);
    }

    public UserDTO toUserDTO(UserModel user) {

        RoleModel role = user.getRole();

        RoleDTO roleDTO = RoleDTO.builder()
            .idRole(role.getIdRole())
            .name(role.getName())
            .description(role.getDescription())
            .build();

        UserDTO userDTO = new UserDTO();
        userDTO.setIdUser(user.getIdUser());
        userDTO.setName(user.getName());
        userDTO.setMiddleName(user.getMiddleName());
        userDTO.setFirstLastName(user.getFirstLastName());
        userDTO.setSecondLastName(user.getSecondLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.getActive());
        userDTO.setIsFirstLogin(user.getIsFirstLogin());
        userDTO.setTwoFactorEnabled(user.getTwoFactorEnabled());
        userDTO.setTwoFactorSecret(user.getTwoFactorSecret());
        userDTO.setVerificationCode(user.getVerificationCode());
        userDTO.setRole(roleDTO);

        userDTO.setCreatedAt(user.getCreatedAt() != null ? dateTimeFormater.formatDateTime(user.getCreatedAt()) : null);
        userDTO.setUpdatedAt(user.getUpdatedAt() != null ? dateTimeFormater.formatDateTime(user.getUpdatedAt()) : null);
        userDTO.setDeleted(user.isDeleted());
        userDTO.setDeletedAT(user.getDeletedAt() != null ? dateTimeFormater.formatDateTime(user.getDeletedAt()) : null);
        return userDTO;
    }

    public BasicUserDTO toBasicUserDTO(UserModel user) {
        return BasicUserDTO.builder()
            .idUser(user.getIdUser())
            .name(user.getName())
            .email(user.getEmail())
            .active(user.getActive())
            .role(user.getRole().getName())
            .build();
    }

}
