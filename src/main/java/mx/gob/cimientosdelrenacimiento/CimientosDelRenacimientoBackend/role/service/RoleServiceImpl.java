package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.dto.RoleDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    
    @Override
    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream()
            .map(role -> RoleDTO.builder()
                .idRole(role.getIdRole())
                .name(role.getName())
                .description(role.getDescription())
                .build()
            ).collect(Collectors.toList());
    }

}
