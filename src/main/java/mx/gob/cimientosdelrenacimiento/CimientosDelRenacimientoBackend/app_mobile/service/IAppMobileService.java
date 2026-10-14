package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service;

import org.springframework.data.domain.Page;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;

public interface IAppMobileService {
    Page<AppMobileGlobalSearchCardDTO> search(String query, int page, int size);
}
