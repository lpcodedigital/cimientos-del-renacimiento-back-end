package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dto;

import lombok.Data;

@Data
public class UserRequestDTO {

    private String name;
    private String middleName;
    private String firstLastName;
    private String secondLastName;
    private String phone;
    private String email;
    private String password;
    private Boolean isFirstLogin;
    private Boolean twoFactorEnabled;    
    private String twoFactorSecret;    
    private Integer verificationCode;
    private Long roleId;

}
