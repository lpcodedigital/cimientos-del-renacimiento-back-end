package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.dto.RoleDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.service.IRoleService;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @RequestMapping("/list")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }
}
