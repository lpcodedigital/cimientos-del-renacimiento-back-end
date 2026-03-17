package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config.CloudflareConfig;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.StorageException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.CloudflareResponse;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.storage.dto.ImageStorageResponse;

@Service
@Slf4j
@ConditionalOnProperty(name = "storage.provider", havingValue = "cloudflare")
public class CloudflareStorageServiceImp implements IImageStorageService{

    private final CloudflareConfig cloudflareConfig;
    private final RestClient restClient;

    public CloudflareStorageServiceImp(CloudflareConfig cloudflareConfig, RestClient.Builder builder) {

        this.cloudflareConfig = cloudflareConfig;
        this.restClient = builder.build();
    }

    @Override
    public ImageStorageResponse upload(MultipartFile file) {

        try {

            log.info("Iniciando subida de imagen a CloudFlare: {}", file.getOriginalFilename());
            
            // Cloudflare requiere un MultipartBody
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            CloudflareResponse response = restClient.post()
                .uri(cloudflareConfig.getApiUrl() + cloudflareConfig.getAccountId() + "/images/v1")
                .header("Authorization", "Bearer " + cloudflareConfig.getApiToken())
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) ->{
                    throw new StorageException("El servicio de almacenamiento externo no respondió correctamente.");
                })
                .body(CloudflareResponse.class);

            if (response == null || !response.success()){ 
                throw new StorageException("Cloudflare no pudo procesar la imagen correctamente.");
            }    
            
            if ( response != null && response.success()){
                // Buscamos las URLs en la lista de variantes
                String publicUrl = response.result().variants().stream()
                    .filter(url -> url.contains("/public/"))
                    .findFirst()
                    .orElse(response.result().variants().get(0  ));
                
                String thumbUrl = response.result().variants().stream()
                    .filter(url -> url.contains("/thumbnail/"))
                    .findFirst()
                    .orElse(publicUrl);

                log.info("Imagen subida exitosamente. ID: {}", response.result().id());
            
                return new ImageStorageResponse(
                    publicUrl, 
                    response.result().id(), 
                    thumbUrl, 
                    file.getContentType(),
                    String.valueOf(file.getSize())
                );
            }

            log.warn("No se pudo procesar la imagen: {}", file.getOriginalFilename());
            //throw new RuntimeException("Resouesta de Cloudflare no exitosa");
            //return mapResponse(response, file);
            throw new StorageException("No se pudo procesar la imagen.");

        }
        catch(StorageException e){
            log.warn("Error controlado en almacenamiento: {}", e.getMessage());
            throw e; // Re-lanzamos la excepción de almacenamiento
        
        } catch (Exception e) {
            log.error("Error crítico e inesperado al subir imagen: ", e); // Pasa la excepción completa para ver el stacktrace
            throw new StorageException("Ocurrió un error inesperado al procesar la imagen: " + e.getMessage());
        } 
        
    }

    @Override
    public void delete(String providerId) {
        try {
            log.info("Intentando eliminar imagen en Cloudflare: {}", providerId);
            restClient.delete()
            .uri(cloudflareConfig.getApiUrl() + cloudflareConfig.getAccountId() + "/images/v1/" + providerId)
            .header("Authorization", "Bearer " + cloudflareConfig.getApiToken())
            .retrieve()
            .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), (req, res) -> {
                /*
                Esto excepcion rompe con la arquitectura de software (Clean code + solid),ya que no hace que sea un metodo resiliente y 
                tenga una responsavilidad unica por lo que devuelve fragil la arquitectura de software.
                 */
                //throw new StorageException("La imagen no existe en el servicio de almacenamiento externo cloudflare.");
                
                //LOGICA CLEAN: Si no existe, imprimimos un aviso, pero no lanzamos una excepción.
                // El objetivo de borrar se considera "exitoso" porque ya no está
                //System.out.println("Aviso: la imagen " + providerId + " ya no existe en el servicio de almacenamiento externo.");
                log.warn("Aviso: la imagen " + providerId + " ya no existe en el servicio de almacenamiento externo.");
            })
            .onStatus(HttpStatusCode::isError, (req, res) ->{
                log.error("Error de comunicación con el servicio de almacenamiento externo al intentar borrar la imagen: {}", providerId);
                throw new StorageException("Error de comunicación con el servicio de almacenamiento externo al intentar borrar la imagen.");  
            })
            .toBodilessEntity();
        } catch (Exception e) {
            log.error("No se pudo eliminar físicamente en Cloudflare: " + providerId);
            //System.err.println("No se pudo eliminar físicamente en Cloudflare: " + providerId);
        }
    }



}
