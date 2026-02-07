package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.ImageStorageResponse;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
public class LocalStorageServiceImpl implements IImageStorageService {

    @Override
    public ImageStorageResponse upload(MultipartFile file) {
        
        return new ImageStorageResponse("url", "providerId", "thumbUrl", "mimeType", "size");
    }

    @Override
    public void delete(String fileName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
