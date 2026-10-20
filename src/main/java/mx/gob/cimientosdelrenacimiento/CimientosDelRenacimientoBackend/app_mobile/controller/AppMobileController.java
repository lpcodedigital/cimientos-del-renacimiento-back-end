package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileCursoDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileObraDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service.IAppMobileService;

@RestController
@RequestMapping("/api/v1/app-mobile")
@RequiredArgsConstructor
public class AppMobileController {

    private final IAppMobileService IAppMobileService;

    // Permitimos acceso a ADMIN, USER y GUEST ya que es para la app pública
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/global-search")
    public ResponseEntity<Page<AppMobileGlobalSearchCardDTO>> globalSearch(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        return ResponseEntity.ok(IAppMobileService.search(search, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/obra/{id}")
    public ResponseEntity<AppMobileObraDetailDTO> getObraDetail(@PathVariable Long id) {
        return ResponseEntity.ok(IAppMobileService.getObraDetail(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/curso/{id}")
    public ResponseEntity<AppMobileCursoDetailDTO> getCursoDetail(@PathVariable Long id) {
        return ResponseEntity.ok(IAppMobileService.getCursoDetail(id));
    }

}
