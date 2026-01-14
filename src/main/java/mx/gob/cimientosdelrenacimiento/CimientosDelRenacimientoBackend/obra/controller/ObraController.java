package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.service.IObraService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/obra")
@RequiredArgsConstructor
public class ObraController {

    private final IObraService obraService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/mapa")
    public ResponseEntity<List<ObraMapaDTO>> getAllForMap() {
        return ResponseEntity.ok(obraService.findAllForObraMapa());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("detail/{id}")
    public ResponseEntity<ObraResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(obraService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/create")
    public ResponseEntity<ObraResponseDTO> create(@Valid @RequestBody ObraRequestDTO request) {
        return new ResponseEntity<>(obraService.create(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ObraResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ObraRequestDTO request) {
        return ResponseEntity.ok(obraService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        obraService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Obra con id " + id + " eliminada exitosamente.");
        return ResponseEntity.ok(response);
    }
}
