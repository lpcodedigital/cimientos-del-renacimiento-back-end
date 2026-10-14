package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service.AppMobileServiceImpl;

@RestController
@RequestMapping("/api/v1/app-mobile")
@RequiredArgsConstructor
public class AppMobileController {

    private final AppMobileServiceImpl AppMobileServiceImpl;

    // Permitimos acceso a ADMIN, USER y GUEST ya que es para la app pública
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/global-search")
    public ResponseEntity<Page<AppMobileGlobalSearchCardDTO>> globalSearch(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        return ResponseEntity.ok(AppMobileServiceImpl.search(search, page, size));
    }

}
