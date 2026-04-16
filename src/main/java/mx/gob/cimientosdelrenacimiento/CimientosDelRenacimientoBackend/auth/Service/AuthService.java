package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.JwtUtils;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.Verify2FARequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.mapper.AuthMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.email.service.IEmailService;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.UnauthorizedException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthBasicUserResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;
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

    @Autowired
    private IEmailService emailService;

    public AuthResponseDTO autenticate(AuthRequestDTO authRequestDTO) {

    var user = userRespository.findByEmail(authRequestDTO.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // 1. Validar la contraseña
        if( !passwordEncoder.matches(authRequestDTO.getPassword(), user.getPassword()) ) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // 2. Verificar el el 2FA está habilitado para el usuario
        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            String verificationCode = String.format("%06d", new Random().nextInt(1000000));
            LocalDateTime codeExpiration = LocalDateTime.now().plusMinutes(5); // El código expira en 5 minutos
            user.setVerificationCode(Integer.parseInt(verificationCode));
            user.setCodeExpiration(codeExpiration);
            userRespository.save(user);

            // Enviar el código de verificación por correo electrónico
            sendVerificationCode2FA(user.getEmail(), user.getName(), verificationCode);

            return AuthResponseDTO.builder()
                .user(authMapper.toAuthBasicUserDTO(user))
                .mfaRequired(true)
                .token(null)
                .build();
        }

        Date expiresAt = jwtUtils.generateExpirationDate();
        String token = jwtUtils.generateJwtToken(
            user.getIdUser(),
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

    public AuthResponseDTO verify2FA(Verify2FARequestDTO request) {
        UserModel user = userRespository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // 1. Validar si el código existe y coincidi
        if (user.getVerificationCode() == null || !user.getVerificationCode().toString().equals(request.getCode())) {
            throw new UnauthorizedException("Código de verificación inválido");
        }

        // 2. Validar si el código ha expirado (Comparar ahora la contra codeExpiration)
        if (user.getCodeExpiration() == null || user.getCodeExpiration().isBefore(LocalDateTime.now())){
            throw new UnauthorizedException("Código de verificación expirado");
        }

        // 3. Todo OK: Generar Token final 
        Date expiresAt = jwtUtils.generateExpirationDate();
        String token = jwtUtils.generateJwtToken(
            user.getIdUser(),
            user.getEmail(),
             user.getRole().getName(), 
             expiresAt
            );
        
        // 4. Limpiar el código de verificación y su expiración
        user.setVerificationCode(null);
        user.setCodeExpiration(null);
        userRespository.save(user);

        return AuthResponseDTO.builder()
            .token(token)
            .expiresAt(expiresAt)
            .user(authMapper.toAuthBasicUserDTO(user))
            .mfaRequired(false)
            .build();

    }

    private void sendVerificationCode2FA(String email, String name, String code) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("code", code);

        try {
            emailService.sendHtmlEmail(
                email, 
                "   Código de Verificación 2FA", 
                "two-factor-code-email", 
                variables);
        } catch (Exception e) {
            // Manejar el error de envío de correo electrónico
            System.err.println("Error al enviar el código 2FA: " + e.getMessage());
        }
    }

}
