package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoPublicDTO;

public interface ICursoService {
    
    Page<CursoPublicDTO> findAllPublic(Pageable pageable);
    
    Page<CursoResponseDTO> findAllAdmin(Pageable pageable);
    
    CursoResponseDTO findById(Long id);
    
    CursoResponseDTO create(CursoRequestDTO requestDTO, List<MultipartFile> files);

    CursoResponseDTO update(Long id, CursoRequestDTO requestDTO, List<MultipartFile> files);

    void delete(Long id);

    // Método para obtener cursos en formato optimizado para el front-end público

}
