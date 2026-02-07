package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config.CloudflareConfig;

@SpringBootApplication
// @EnableJpaAuditing
@EnableConfigurationProperties(CloudflareConfig.class)
public class CimientosDelRenacimientoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CimientosDelRenacimientoBackendApplication.class, args);
	}

}
