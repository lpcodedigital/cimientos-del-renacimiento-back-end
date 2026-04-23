package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoPublicDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.mapper.CursoMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.CursoRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ResourceNotFoundException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.ImageStorageResponse;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.service.IImageStorageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CursoServiceImpl implements ICursoService {

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;
    private final IImageStorageService imageStorageService; 

    @Override
    @Transactional(readOnly = true)
    public Page<CursoPublicDTO> findAllPublic(Pageable pageable) {
        
        return cursoRepository.findAllPublicPaginated(pageable)
                .map(cursoMapper::projectionToPublicDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CursoResponseDTO> findAllAdmin(Pageable pageable) {
        
        return cursoRepository.findAllAdminPaginated(pageable)
                .map(cursoMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO findById(Long id) {
        
        return cursoRepository.findById(id)
                .map(cursoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public CursoResponseDTO create(CursoRequestDTO requestDTO,
            List<MultipartFile> files) {
       
        // 1.- Mappear el DTO a la entidad
        CursoModel curso = cursoMapper.toEntity(requestDTO);

        // 2. Validar la cantidad de imágenes subidas
        if (files.size() > 11) { // 1 portada + 10 galería
            throw new IllegalArgumentException("No se pueden subir más de 10 imágenes por curso.");
        }

        // 3.- Procesar galería si existen imágenes
        if (files != null && !files.isEmpty()) {

            for (int i = 0; i < files.size(); i++) {
                MultipartFile image = files.get(i);
                if (!image.isEmpty() && image.getSize() > 0) { 

                    CursoImageModel imageModel = uploadImage(image, i ); // La portada es posición 1, la galería empieza en 2
                    
                    curso.addImage(imageModel);

                    // Asignar la portada al primer archivo de la lista (si existe)
                    if (i == 0) {
                        curso.setCoverImage(imageModel);
                    }
                }
            }

        }

        return cursoMapper.toResponseDTO(cursoRepository.save(curso));
    }

    @Override
    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO requestDTO,
            List<MultipartFile> files) {
        
        CursoModel curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        
        // Actualizar campos básicos
        cursoMapper.updateEntity(curso, requestDTO);

        // Obtener IDs de imágenes que se deben mantener (si se proporcionan)
        List<Long> keepingImageIds = requestDTO.getKeepImageIds() != null ? requestDTO.getKeepImageIds() : new ArrayList<>();

        // Identificar imágenes a eliminar (las que no están en keepingImageIds)
        List<CursoImageModel> imagesToRemove = curso.getImages().stream()
                .filter(img -> !keepingImageIds.contains(img.getId()))
                .collect(Collectors.toList());
        
        // Eliminar imágenes no deseadas
        imagesToRemove.forEach(img -> {
            if( curso.getCoverImage() != null && curso.getCoverImage().getId().equals(img.getId())){
                curso.setCoverImage(null); // Si la imagen a eliminar es la portada, desvincularla
            }
            deleteFromStorage(img); // Eliminar del storage remoto
            curso.getImages().remove(img);
        });

        // Procesar nuevas imágenes si se proporcionan
        if (files != null && !files.isEmpty()) {

            // Filtrar solo las imágenes válidas (no vacías)
            List<MultipartFile> validGalleryImages = files.stream()
                    .filter(img -> img != null && !img.isEmpty() && img.getSize() > 0)
                    .collect(Collectors.toList());
            
            if (!validGalleryImages.isEmpty()) {

                // Validar que la cantidad total de imágenes (existentes + nuevas) no exceda el límite
                if (curso.getImages().size() + validGalleryImages.size() > 11) {
                    throw new IllegalArgumentException("No se pueden subir más de 11 imágenes por curso.");
                }

                for (int i = 0; i < validGalleryImages.size(); i++) {
                    MultipartFile image = validGalleryImages.get(i);
                    
                    if (i == 0) {

                        if (curso.getCoverImage() != null) {
                            deleteFromStorage(curso.getCoverImage()); // Eliminar la portada anterior del storage remoto
                            curso.setCoverImage(null); // Desvincular la portada anterior
                        }
                        // Si no hay portada asignada, la primera imagen válida se convierte en portada
                        CursoImageModel newCoverImage = uploadImage(image, 0); // Posición 0 para la portada
                        curso.setCoverImage(newCoverImage);
                        curso.addImage(newCoverImage);
                    } else {
                        // Las demás imágenes se agregan a la galería
                        CursoImageModel NewGalleryImage = uploadImage(image, curso.getImages().size()); // Posición secuencial para la galería
                        curso.addImage(NewGalleryImage);
                    }
                }
            }
        }


        return cursoMapper.toResponseDTO(cursoRepository.save(curso));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        
        CursoModel curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        curso.setDeleted(true);
        curso.setDeletedAt(LocalDateTime.now());

        log.info("Iniciando proceso de borrado (soft delete) para el curso: {}", curso.getTitle());
                

        curso.getImages().forEach(img -> {
            img.setDeleted(true);
            img.setDeletedAt(LocalDateTime.now());

            deleteFromStorage(img); // Eliminar cada imagen del storage remoto
        });

        cursoRepository.saveAndFlush(curso);

        cursoRepository.delete(curso);
    }

    private CursoImageModel uploadImage(MultipartFile image, Integer position) {
        ImageStorageResponse response = imageStorageService.upload(image);
        CursoImageModel cursoImageModel = new CursoImageModel();
        cursoImageModel.setUrl(response.url());
        cursoImageModel.setProviderId(response.providerId());
        cursoImageModel.setThumbUrl(response.thumbUrl());
        cursoImageModel.setMimeType(response.mimeType());
        cursoImageModel.setSize(response.size());
        cursoImageModel.setPosition(position);
        return cursoImageModel;
    }

    private void deleteFromStorage(CursoImageModel image) {
        try {
            imageStorageService.delete(image.getProviderId());
            log.info("Imagen eliminada de CloudFlare exitosamente: {}", image.getUrl());
        } catch (Exception e) {
            log.error("Error al eliminar la imagen del storage remoto {}: {}", image.getProviderId(), e.getMessage());
        }
    }

}
