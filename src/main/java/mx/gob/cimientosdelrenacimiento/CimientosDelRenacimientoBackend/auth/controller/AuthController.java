package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.auth.controller;

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
}
