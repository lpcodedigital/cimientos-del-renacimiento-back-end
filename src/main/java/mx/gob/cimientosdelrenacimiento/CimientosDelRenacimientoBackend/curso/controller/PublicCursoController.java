package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoPublicDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.service.ICursoService;

@RestController
@RequestMapping("/api/v1/public/curso")
@RequiredArgsConstructor
public class PublicCursoController {

    private final ICursoService cursoService;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllPublic(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<CursoPublicDTO> publicPage = cursoService.findAllPublic(PageRequest.of(page, size));
        
        // Estandarización de la respuesta para el front-end refine
        // Enviamos 'data' y 'total' para que el dataprovider no tenga que adivinar
        Map<String, Object> response = new HashMap<>();
        response.put("data", publicPage.getContent());
        response.put("total", publicPage.getTotalElements());

        return ResponseEntity.ok(response);
        
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CursoResponseDTO> getById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(cursoService.findById(id));
    }

}
