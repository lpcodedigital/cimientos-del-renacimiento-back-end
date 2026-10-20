package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service;

import org.springframework.data.domain.Page;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileCursoDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileObraDetailDTO;

public interface IAppMobileService {
    Page<AppMobileGlobalSearchCardDTO> search(String query, int page, int size);

    AppMobileObraDetailDTO getObraDetail(Long obraId);

    AppMobileCursoDetailDTO getCursoDetail(Long cursoId);
}
