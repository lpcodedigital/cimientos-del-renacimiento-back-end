package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.Service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.JwtUtils;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.mapper.AuthMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.UnauthorizedException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthBasicUserResponseDTO;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRespository userRespository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthMapper authMapper;

    public AuthResponseDTO autenticate(AuthRequestDTO authRequestDTO) {

    var user = userRespository.findByEmail(authRequestDTO.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if( !passwordEncoder.matches(authRequestDTO.getPassword(), user.getPassword()) ) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        Date expiresAt = jwtUtils.generateExpirationDate();

        String token = jwtUtils.generateJwtToken(
            user.getEmail(),
             user.getRole().getName(), 
             expiresAt
            );

        AuthBasicUserResponseDTO authBasicUserResponseDTO = authMapper.toAuthBasicUserDTO(user);

        return AuthResponseDTO.builder()
            .token(token)
            .expiresAt(expiresAt)
            .user(authBasicUserResponseDTO)
            .build();
    }

}
