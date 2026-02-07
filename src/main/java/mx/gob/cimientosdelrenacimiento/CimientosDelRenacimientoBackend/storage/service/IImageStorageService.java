package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.service;

import org.springframework.web.multipart.MultipartFile;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.ImageStorageResponse;


public interface IImageStorageService {

    ImageStorageResponse upload(MultipartFile file);

    void delete(String providerId);

}
