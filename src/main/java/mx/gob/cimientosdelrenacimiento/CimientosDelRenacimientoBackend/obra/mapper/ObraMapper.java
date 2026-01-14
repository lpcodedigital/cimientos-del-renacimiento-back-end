package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraImageDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraMapaDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.dto.ObraResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;

// Con este decorador se indica que es un mapper de MapStruct y cuando se genera la clase que implementa esta interfaz, se registre como un @component de Spring.
// Gracias a esto se puede inyectar directamente en los controladores o servicios.
@Mapper(componentModel = "spring")
public interface ObraMapper {

    // -- ENTIDAD A MAPA DTO (Optimizado) -- //
    ObraMapaDTO toObraMapaDTO(ObraModel obra);
    List<ObraMapaDTO> toObraMapaDTOList(List<ObraModel> obras);

    // -- ENTIDAD A OBRA RESPONSE DTO -- //
    @Mapping(source = "createdBy.email", target = "createdBy") // Extrae el email del UserModel de Auditable
    @Mapping(source = "status", target = "status") // Convierte el enum a String automáticamente
    ObraResponseDTO toObraResponseDTO(ObraModel obra);

    // -- OBRA REQUEST DTO A ENTIDAD -- //
    // MapStruct usa un metodo calificador @Named("mapUrlsToImages") para resolver la anbigüedades cuando MapStruct no sabe cómo automaticamente mapear o convertir un tipo de dato a otro (por ejemplo, List<String> a List<ObraImageModel>.)
    @Mapping(target = "id", ignore = true) // Ignorar el ID al mapear desde el request
    @Mapping(target = "createdAt", ignore = true) 
    @Mapping(target = "createdBy", ignore = true) 
    @Mapping(target = "deleted", ignore = true) 
    @Mapping(target = "deletedAt", ignore = true) 
    @Mapping(target = "updatedAt", ignore = true) 
    @Mapping(target = "updatedBy", ignore = true) 
    @Mapping(target = "images", source = "imagesUrls", qualifiedByName = "mapUrlsToImages") // Con qualifiedByName MapStruct busca en el mismo archivo un metodod que tenga la anotacion @Named("mapUrlsToImages")
    ObraModel toObraModel(ObraRequestDTO obraRequestDTO);

    // -- ENTIDAD A IMAGEN DTO -- //
    ObraImageDTO toObraImageDTO(ObraImageModel image);

    // Logica personalizada para convertir String URLs a ObraImageModel
    @Named("mapUrlsToImages")
    default List<ObraImageModel> mapUrlsToImages(List<String> urls) {
        if (urls == null) return null;
        return urls.stream().map( url -> {
            ObraImageModel img = new ObraImageModel();
            img.setUrl(url);
            return img;
        }).collect(Collectors.toList());
    }
}
