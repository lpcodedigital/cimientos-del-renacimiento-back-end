package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CimientosDelRenacimientoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CimientosDelRenacimientoBackendApplication.class, args);
	}

}
