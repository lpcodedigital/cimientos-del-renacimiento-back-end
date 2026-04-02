package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraLinkProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.service.IObraService;

@RestController
@RequestMapping("/api/v1/public/obra")
@RequiredArgsConstructor
public class PublicObraController {

    private final IObraService obraService;

    /**
     * Endpoint público para obtener todas las obras optimizadas para el mapa.
     * No requiere token JWT.
     */
    @GetMapping("/mapa")
    public ResponseEntity<List<ObraMapaDTO>> getAllForMap() { 
        return ResponseEntity.ok(obraService.findAllForObraMapa()); 
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<ObraResponseDTO> getBEntity(@PathVariable Long id) {
        return ResponseEntity.ok(obraService.findById(id));
    }

    /**
     * Endpoint público para obtener obras por municipio de la tabla.
     * No requiere token JWT.
    
     * @param municipio
     * @return
     */
    
    @GetMapping("/municipio/{municipio}")
    public ResponseEntity<List<ObraLinkProjection>> getObrasByMunnicipio(@PathVariable String municipio) {
        List<ObraLinkProjection> obras = obraService.getObrasByMunicipioPublic(municipio);
        return ResponseEntity.ok(obras);
    }

    /*
     * Endpoint público para obtener el detalle de una obra específica de la tabla.
     * No requiere token JWT.
     *
     * @param id
     * @return
     */

    @GetMapping("/municipio-obra-detail/{id}")
    public ResponseEntity<ObraResponseDTO> getObraDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(obraService.findById(id));
    }
    

}
