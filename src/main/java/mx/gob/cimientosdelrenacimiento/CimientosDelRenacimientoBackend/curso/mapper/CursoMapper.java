package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoImageDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoPublicDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.dto.CursoResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.projections.CursoPaginationProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model.MunicipioModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.repository.MunicipioRepository;

@Mapper(componentModel = "spring")
public abstract class CursoMapper {

    @Autowired
    protected MunicipioRepository municipioRepository;

    /*
        Nota:
        - target: Es el nombre del atributo en el objeto que está devolviendo el método.
        - source: Es de dónde viene el dato en el objeto que recibes como parámetro.
    */

    // Entidad a Public DTO
    @Mapping(source = "municipality.name", target = "municipalityName")
    @Mapping(source = "coverImage.url", target = "coverImageUrl") // Mapeo directo de coverImageId a coverImageUrl
    public abstract CursoPublicDTO toPublicDTO(CursoModel cursoModel);

    // Proyeccion a Public DTO (maxima optimizacion para el front)
    // MapStruct mapeará automáticamente los campos que tengan el mismo nombre, así que solo necesitamos mapear los que tienen nombres diferentes o requieren lógica personalizada.
    public abstract CursoPublicDTO projectionToPublicDTO(CursoPaginationProjection projection);

    // Lista de Entidades a Lista de Public DTOs
    public abstract List<CursoPublicDTO> toPublicDtoList(List<CursoModel> cursos);

    // Entidad a Response DTO
    @Mapping(source = "municipality.name", target = "municipalityName")
    @Mapping(source = "municipality.id", target = "municipalityId")
    @Mapping(source = "createdBy.email", target = "createdBy")
    @Mapping(source = "updatedBy.email", target = "updatedBy")
    @Mapping(source = "coverImage", target = "coverImage")
    public abstract CursoResponseDTO toResponseDTO(CursoModel cursoModel);

    // Request DTO a Entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true) // Ignorar el mapeo de imágenes aquí, se manejará manualmente en el servicio
    @Mapping(target = "coverImage", ignore = true) // Ignorar el mapeo de coverImageId, se manejará manualmente en el servicio
    @Mapping(target = "municipality", source = "municipalityId", qualifiedByName = "idToMunicipio")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract CursoModel toEntity(CursoRequestDTO requestDTO);

    // Entidad ImageModel a ImageDTO
    public abstract CursoImageDTO toImageDTO(CursoImageModel image);

    /**
     * Este método actualiza la instancia 'curso' que ya existe en la BD
     * con los datos que vienen en el 'dto'.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    @Mapping(target = "municipality", source = "municipalityId", qualifiedByName = "idToMunicipio")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateEntity(@MappingTarget CursoModel curso, CursoRequestDTO requestDTO);

    // Logica personalizada
    @Named("idToMunicipio")
    protected MunicipioModel idToMunicipio(Long id) {
        if (id == null) return null;
        return municipioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Municipio no encontrado con ID: " + id));
    }
}
