package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service;

import java.util.List;

import org.springframework.data.domain.Page;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileCursoDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileObraDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileLightResponseDTO;

public interface IAppMobileService {
    Page<AppMobileGlobalSearchCardDTO> search(String query, int page, int size);

    AppMobileObraDetailDTO getObraDetail(Long obraId);

    AppMobileCursoDetailDTO getCursoDetail(Long cursoId);

    List<AppMobileLightResponseDTO> getMapItemsByMunicipality(String municipality);
}
