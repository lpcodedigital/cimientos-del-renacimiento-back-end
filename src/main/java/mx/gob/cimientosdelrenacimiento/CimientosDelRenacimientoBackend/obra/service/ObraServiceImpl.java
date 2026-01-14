package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ConflictException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ResourceNotFoundException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.mapper.ObraMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraMapaProjection;

@Service
@RequiredArgsConstructor
@Transactional
public class ObraServiceImpl implements IObraService {

    private final ObraRespository obraRespository;
    private final ObraMapper obraMapper;
    @Override
    public ObraResponseDTO create(ObraRequestDTO request) {
       
        // Validar si la obra ya existe por nombre

        obraRespository.findAnyByNombre(request.getName()).ifPresent(o -> {
            throw new ConflictException("Ya existe una obra con el nombre: " + request.getName());
        });

        // Mapear el request a la entidad
        // Gracias al @Named del mapper, aquí ya tenemos la lista de ObraImageModel
        ObraModel obra = obraMapper.toObraModel(request);

        // !IMPORTANTE! Establecer la relación bidireccional entre ObraModel y ObraImageModel
        if (obra.getImages() != null) {

            // Crea una copia de las imagenes que traia el objeto mapeado
            List<ObraImageModel> images = new ArrayList<>(obra.getImages());
            
            // Limpiar la lista original de la entidad para evitar problemas de persistencia
            obra.getImages().clear(); 
            
            // Version larga usando forEach
            // Ejemplo: images.forEach(image -> obra.addImage(image));

            // Version corta usando forEach
            images.forEach(obra::addImage); // Volvemos a agregar las imagenes usando el método addImage que establece la relación correctamente
        }

        // Guardar la obra en la base de datos
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
    public ObraResponseDTO update(Long id, ObraRequestDTO request) {
       
        ObraModel obra = obraRespository.findByIdWithImages(id).orElseThrow( () ->
            new ResourceNotFoundException("No se encontró la obra con ID: " + id)
        );

        // Validar si el nombre (si cambió, verificar que no este duplicado)
        if (!obra.getName().equalsIgnoreCase(request.getName())){

            obraRespository.findAnyByNombre(request.getName()).ifPresent( o -> {
                throw new ConflictException("Ya existe una obra con el nombre: " + request.getName());
            });
        }

        // Actualizar los campos básicos de la obra
        obra.setName(request.getName());
        obra.setAgency(request.getAgency());
        obra.setMunicipality(request.getMunicipality());
        obra.setInvestment(request.getInvestment());
        obra.setProgress(request.getProgress());
        obra.setDescription(request.getDescription());
        obra.setLatitude(request.getLatitude());
        obra.setLongitude(request.getLongitude());
        obra.setStatus(request.getStatus());

        /* Este es un ejemplo de control manual de las imagenes */
        /*

        // Sincronizar las imágenes (Logica: Reemplazo total de las imágenes)
        // Borramos las imagenes anteriores agregamos las nuevas del DTO
        obra.getImages().clear();
        if(request.getImagesUrls() != null) {
            
        request.getImagesUrls().forEach( url -> {
            ObraImageModel newImage = new ObraImageModel();
            newImage.setUrl(url);
            obra.addImage(newImage);
        });
    }

        // Guardar los cambios en la base de datos
        ObraModel updatedObra = obraRespository.save(obra);
        
        return obraMapper.toObraResponseDTO(updatedObra);

        */

        /* Este es un ejemplo de control automático de las imagenes */
        // MANEJO DE IMAGENES USANDO EL MAPPER

        // Delgamos la conversion de String -> ObraImageModel al mapper usando el metodo calificador @Named("mapUrlsToImages")
        List<ObraImageModel> newImages = obraMapper.mapUrlsToImages(request.getImagesUrls());

        // Borramos las imagenes anteriores
        obra.getImages().clear();

        // Agregamos las nuevas imagenes usando el metodo addImage que establece la relación bidireccional correctamente
        if(newImages != null) {
            newImages.forEach(obra::addImage);
        }
         return obraMapper.toObraResponseDTO(obraRespository.save(obra));

    }

    @Override
    @Transactional
    public void delete(Long id) {
        
        ObraModel obra = obraRespository.findById(id).orElseThrow(() ->
             new ResourceNotFoundException("No se puede eliminar: Obra no encontrada")
            );
        
        // Soft delete
        obra.setDeleted(true);
        obra.setDeletedAt(LocalDateTime.now());
        
    }

}
