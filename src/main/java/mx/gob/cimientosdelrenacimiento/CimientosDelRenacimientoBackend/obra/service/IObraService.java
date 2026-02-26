package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseListDTO;

public interface IObraService {

    // Para crear una nueva obra
    ObraResponseDTO create(ObraRequestDTO request, List<MultipartFile> files);

    // Para obtener todas las obras en formato mapa con datos optimizados
    List<ObraMapaDTO> findAllForObraMapa();

    // Para obtener una obra por su ID
    ObraResponseDTO findById(Long id);

    // Para actualizar una obra existente
    ObraResponseDTO update(Long id, ObraRequestDTO request, List<MultipartFile> files);

    // Para eliminar una obra por su ID
    void delete(Long id);

    // Para obtener una lista paginada de obras usando la proyección personalizada
    Page<ObraResponseListDTO> findAllPaginated(int page, int size);
}
