package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.dto.MunicipioResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model.MunicipioModel;

@Mapper(componentModel = "spring")
public interface MunicipioMapper {

    // ENTIDAD A DTO
    MunicipioResponseDTO toResponseDTO(MunicipioModel model);

    // LISTA DE ENTIDADES A LISTA DE DTO
    List<MunicipioResponseDTO> toResponseDTOList(List<MunicipioModel> models);
}
