package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ConflictException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ResourceNotFoundException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseListDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.mapper.ObraMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraMapaProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraPaginationProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.ImageStorageResponse;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.service.IImageStorageService;

@Service
@RequiredArgsConstructor
@Transactional
public class ObraServiceImpl implements IObraService {

    private final ObraRespository obraRespository;
    private final ObraMapper obraMapper;
    private final IImageStorageService imageStorageService;
    @Override
    public ObraResponseDTO create(ObraRequestDTO request, List<MultipartFile> files) {
       
        // 1. Validar si la obra ya existe por nombre

        obraRespository.findAnyByNombre(request.getName()).ifPresent(o -> {
            throw new ConflictException("Ya existe una obra con el nombre: " + request.getName());
        });

        // 2. Mapear el request DTO a la entidad
        // Gracias al @Named del mapper, aquí ya tenemos la lista de ObraImageModel
        ObraModel obra = obraMapper.toObraModel(request);

        // 3. Validar la cantidad de imágenes subidas
        if (files.size() > 10) {
            throw new IllegalArgumentException("No se pueden subir más de 10 imágenes por obra.");
        }

        // 4. Procesar y subir cada imagen usando el servicio de almacenamiento
        if (files != null && !files.isEmpty()) {

            files.forEach(file -> {

                // Solo procesar la imagen si no está vacía y tiene contenido
                if (!file.isEmpty() && file.getSize() > 0) {
                    
                    // 1. Subir al proveedor
                    ImageStorageResponse response = imageStorageService.upload(file);
   
                    // 2. Crear modelo de imagen con los datos del proveedor
                    ObraImageModel imgModel = new ObraImageModel();
                    imgModel.setUrl(response.url());
                    imgModel.setProviderId(response.providerId());
                    imgModel.setThumbUrl(response.thumbUrl());
                    imgModel.setMimeType(response.mimeType());
                    imgModel.setSize(response.size());
                    imgModel.setPosition(null);
   
                    // 3. Agregar la imagen a la obra (establece la relación bidireccional)
                    obra.addImage(imgModel); 
                }
                 
            });
        }

        // 5. Guardar la obra en la base de datos
        ObraModel obraSaved = obraRespository.save(obra);
        
        return obraMapper.toObraResponseDTO(obraSaved);
    }

    @Override
    @Transactional(readOnly = true) 
    public List<ObraMapaDTO> findAllForObraMapa() {
        // 1. Obtenemos las proyecciones (interfaces automáticas de JPA)
        List<ObraMapaProjection> proyecciones = obraRespository.findAllForMap();

        // 2. Convertimos la lista de Proyecciones a la lista de DTOs que espera tu controlador
        return proyecciones.stream().map(p -> {
            ObraMapaDTO dto = new ObraMapaDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setLatitude(p.getLatitude());
            dto.setLongitude(p.getLongitude());
            dto.setMunicipality(p.getMunicipality());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ObraResponseDTO findById(Long id) {
        
        // Usamos la consulta optimizada que carga las imágenes con JOIN FETCH para evitar el problema N+1 de las imágenes
        ObraModel obra = obraRespository.findByIdWithImages(id).orElseThrow(() -> 
            new ResourceNotFoundException("No se encontró la obra con ID: " + id)
        );

        return obraMapper.toObraResponseDTO(obra);

    }

    @Override
    public ObraResponseDTO update(Long id, ObraRequestDTO request, List<MultipartFile> newFiles) {
       
        // 1. Obtener la obra existente
        ObraModel obra = obraRespository.findByIdWithImages(id).orElseThrow( () ->
            new ResourceNotFoundException("No se encontró la obra con ID: " + id)
        );

        // 2. Validar si el nombre (si cambió, verificar que no este duplicado)
        if (!obra.getName().equalsIgnoreCase(request.getName())){

            obraRespository.findAnyByNombre(request.getName()).ifPresent( o -> {
                throw new ConflictException("Ya existe una obra con el nombre: " + request.getName());
            });
        }

        // 3. Actualizar los campos básicos de la obra
        obra.setName(request.getName());
        obra.setAgency(request.getAgency());
        obra.setMunicipality(request.getMunicipality());
        obra.setInvestment(request.getInvestment());
        obra.setProgress(request.getProgress());
        obra.setDescription(request.getDescription());
        obra.setLatitude(request.getLatitude());
        obra.setLongitude(request.getLongitude());
        obra.setStatus(request.getStatus());

        // 4. Gestion de imagenes existentes
        List<Long> keepImageIds = request.getKeepImageIds() != null ? request.getKeepImageIds() : new ArrayList<>();

        // 5. Identificar cules se deben eliminar
        List<ObraImageModel> imagesToRemove = obra.getImages().stream()
            .filter(img -> !keepImageIds.contains(img.getId()))
            .collect(Collectors.toList());

        // 6. Eliminar las imagenes
        imagesToRemove.forEach(img -> {
            // Borrado fisico en el proveedor
            try {
                imageStorageService.delete(img.getProviderId());
            } catch (Exception e) {
                // Loguear el error pero continuar con la eliminación si es critico mantener la coherencia
                System.err.println("Error al eliminar la imagen en storage: " + img.getProviderId() + " - " + e.getMessage());
            }
            // Eliminar de la colección de la obra
            obra.getImages().remove(img); 
        });

        // 7. Procesar las nuevas imagenes a agregar
        if (newFiles != null && !newFiles.isEmpty()) {
             
            // Validar la cantidad total de imagenes despues de agregar las nuevas
            if (obra.getImages().size() + newFiles.size() > 10){
                throw new IllegalStateException("No se pueden agregar más de 10 imágenes a una obra.");
            }

            newFiles.forEach(file -> {
                
                ImageStorageResponse response = imageStorageService.upload(file);
                ObraImageModel imgModel = new ObraImageModel();

                imgModel.setUrl(response.url());
                imgModel.setProviderId(response.providerId());
                imgModel.setThumbUrl(response.thumbUrl());
                imgModel.setMimeType(response.mimeType());
                imgModel.setSize(response.size());
                imgModel.setPosition(null);

                obra.addImage(imgModel);
            });
        }
       
        return obraMapper.toObraResponseDTO(obraRespository.save(obra));

    }

    @Override
    @Transactional
    public void delete(Long id) {
        
        // 1.- Obtener la obra con sus imágenes para poder eliminarlas también
        ObraModel obra = obraRespository.findById(id).orElseThrow(() ->
             new ResourceNotFoundException("No se puede eliminar: Obra no encontrada")
            );
        
        // Soft delete
        /*
            1. Marcar la obra como borrada para que JPA Auditing detecte 
            el cambio en la entidad y llene 'updated_at' y 'deleted_by'
        */ 
        obra.setDeleted(true);
        obra.setDeletedAt(LocalDateTime.now());

        // 2. Al guardar, JPA Auditing pondra al usuario actual en 'updated_by'
        //obraRespository.save(obra);

        /*
            3. Las imagenes tambien se mantienen con el 'updated_by' y 'deleted_at' 
            del que borra la obra, hay que recorrerlas y marcarlas como borradas
        */
        obra.getImages().forEach(img -> {
            img.setDeleted(true);
            img.setDeletedAt(LocalDateTime.now());

            // Intentar borrar en Cloudflare mientras recorremos las imágenes, así evitamos tener que hacer un segundo recorrido solo para eso
            //try {
            //    imageStorageService.delete(img.getProviderId());
            //} catch (Exception e) {
            //    // Loguear el error pero continuar con la eliminación si es critico mantener la coherencia
            //    System.err.println("Error al eliminar la imagen en storage: " + img.getProviderId() + " - " + e.getMessage());
            //}
        });

        // 4. ¡PASO CLAVE!: Sincronizar cambios de auditoría
        // Esto obliga a JPA a ejecutar los UPDATES de auditoría (updated_by) AHORA.
        obraRespository.saveAndFlush(obra);
        
        // 5. Ejecutar el Soft Delete final
        // Ahora que la auditoría ya se guardó, procedemos al borrado lógico
        /* Nota: Al ejecutar el delete de la obra automaticamente se marca deleted = true 
            no se elimina fisicamente de la base de datos gracias a la anotación @SQLDelete
            en la entidad ObraModel y las imagnes se marcan como deleted = true pero no se puede 
            tener una audtioria para saber quien hizo ese cambio en el estado de las imagenes.
        */
        obraRespository.delete(obra);
    
        // Al ejecutar delete(obra):
        // - Se dispara el SQL de @SQLDelete de la Obra.
        // - Se dispara el SQL de @SQLDelete de cada Imagen (por el Cascade).
        //   siempre y cuando el contexto de seguridad tenga al usuario.
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResponseListDTO> findAllPaginated(int page, int size) {
        
        // Definimos paginación y ordenamiento por fecha de creación descendente
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

       Page<ObraPaginationProjection> projections = obraRespository.findAllPaginated(pageable);

        // Convertimos la página de proyecciones a página de DTOs
        // Como no queremos imágenes, el campo 'images' en ObraResponseListDTO quedará como null, lo cual es correcto para esta vista de paginación
    
        return projections.map(p -> {
            ObraResponseListDTO dto = new ObraResponseListDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setMunicipality(p.getMunicipality());
            dto.setDescription(p.getDescription());
            dto.setStatus(p.getStatus().toString());
            dto.setProgress(p.getProgress());
            dto.setCreatedAt(p.getCreatedAt());
            return dto;
        });
        
    }

}
