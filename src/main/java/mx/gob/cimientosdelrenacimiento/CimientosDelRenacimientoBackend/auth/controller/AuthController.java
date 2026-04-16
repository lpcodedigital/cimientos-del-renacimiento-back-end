package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.Service.AuthService;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.AuthResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.Resend2FADTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.dto.Verify2FARequestDTO;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {

        AuthResponseDTO authResponseDTO = authService.autenticate(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
        
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponseDTO> verify2FA(@Valid @RequestBody Verify2FARequestDTO verify2FARequestDTO) {

        AuthResponseDTO authResponseDTO = authService.verify2FA(verify2FARequestDTO);
        return ResponseEntity.ok(authResponseDTO);
        
     }

     @PostMapping("/resend-2fa")
     public ResponseEntity<Map<String, String>> resend2FA(@Valid @RequestBody Resend2FADTO resend2fadto) {
        authService.resend2FACode(resend2fadto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Código de verificación reenviado exitosamente");
        return ResponseEntity.ok(response);
     }
}
