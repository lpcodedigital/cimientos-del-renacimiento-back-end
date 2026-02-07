package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cloudflare")
@Data
public class CloudflareConfig {
    private String accountId;
    private String apiToken;
    private String apiUrl;
    private String deliveryUrl;
}
