package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long idUser;
    private String name;
    private String middleName;
    private String firstLastName;
    private String secondLastName;
    private String phone;
    private String email;
    private Boolean isFirstLogin;
    private Boolean twoFactorEnabled;    
    private String twoFactorSecret;    
    private Integer verificationCode;
    private RoleDTO role;
    private Boolean deleted;
    private String createdAt;
    private String updatedAt;
    private String deletedAT;
}
