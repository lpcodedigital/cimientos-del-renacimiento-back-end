package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.service.ICursoService;

@RestController
@RequestMapping("/api/v1/curso")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService cursoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')") 
    @GetMapping("/list")
    public ResponseEntity<Page<CursoResponseDTO>> getAllAdmin(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search

    ) {
        return ResponseEntity.ok(cursoService.findAllAdmin(page, size, search));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/detail/{id}")
    public ResponseEntity<CursoResponseDTO> getById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(cursoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "/create", consumes = { "multipart/form-data" })
    public ResponseEntity<CursoResponseDTO> create(
        @RequestPart("request") @Valid CursoRequestDTO request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(cursoService.create(request, files));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping(value ="/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<CursoResponseDTO> update(
        @PathVariable Long id,
        @RequestPart("request") @Valid CursoRequestDTO request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(cursoService.update(id, request, files));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(
        @PathVariable Long id
    ) {
        cursoService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Curso eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
