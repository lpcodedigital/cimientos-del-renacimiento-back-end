package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.mapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileCursoDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileObraDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;

@Mapper(componentModel = "spring")
public interface AppMobileMapper {


    // Mapeo de obra
    @Mapping(source = "images", target = "galeryUrls", qualifiedByName = "mapImagesToUrls") // Map the GaleryUrls field from ObraModel to AppMobileObraDetailDTO
    AppMobileObraDetailDTO toAppMobileObraDetailDTO(ObraModel obra);

    // Mapeo de curso
    @Mapping(target = "coverImageUrl", source = "images", qualifiedByName = "mapImageToCoverImageUrl") // Map the coverImageUrl field from CursoModel to AppMobileCursoDetailDTO
    @Mapping(target = "galeryUrls", source = "images", qualifiedByName = "mapImagesToUrls")
    @Mapping(target = "municipality", source = "municipality.name") // Map the municipality name from CursoModel to AppMobileCursoDetailDTO
    AppMobileCursoDetailDTO toAppMobileCursoDetailDTO(CursoModel curso);

    // -- LÓGICA PERSONALIZADA PARA OBRA --
    @Named("mapImagesToUrls")
    default List<String> extractObraGalleryUrls(List<ObraImageModel> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(ObraImageModel::getUrl)
                .collect(Collectors.toList());
    }

    // -- LÓGICA PERSONALIZADA PARA CURSO (Posiciones) --
    @Named("mapImageToCoverImageUrl")
    default String mapImageToCoverImageUrl(List<CursoImageModel> images) {
        if (images == null) return null;
        return images.stream()
                .filter(img -> img.getPosition() != null && img.getPosition() == 0)
                .map(CursoImageModel::getUrl)
                .findFirst()
                .orElse(null);
    }

    @Named("mapImagesToUrls")
    default List<String> extractCursoGallery(List<CursoImageModel> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                //.filter(img -> img.getPosition() != null && img.getPosition() > 0)
                .sorted(Comparator.comparing(CursoImageModel::getPosition))
                .map(CursoImageModel::getUrl)
                .collect(Collectors.toList());
    }
    
}