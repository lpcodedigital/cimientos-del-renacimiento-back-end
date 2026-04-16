package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.dto;

import java.time.LocalDateTime;

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
    private Boolean active;
    private Boolean isFirstLogin;
    private Boolean twoFactorEnabled;    
    private String twoFactorSecret;    
    private Integer verificationCode;
    private LocalDateTime codeExpiration;
    private Long roleId;

}
