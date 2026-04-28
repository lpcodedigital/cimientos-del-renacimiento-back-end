package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.dto.MunicipioResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.service.IMunicipioService;

@RestController
@RequestMapping("/api/v1/municipio")
@RequiredArgsConstructor
public class MunicipioController {

    private final IMunicipioService municipioService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAll() {
        List<MunicipioResponseDTO> municipios = municipioService.findAll();
        
        // Estandarizamos igual que en Cursos para el DataProvider
        Map<String, Object> response = new HashMap<>();
        response.put("data", municipios);
        response.put("total", municipios.size());
        
        return ResponseEntity.ok(response);
    }

}
