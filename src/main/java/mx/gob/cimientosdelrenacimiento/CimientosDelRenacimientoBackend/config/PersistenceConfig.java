package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.details.UserPrincipal;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public AuditorAware<UserModel> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty();
            }

                
            // Recuperamos nuestro Userprincipal desde el contexto de seguridad
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();  
            
            // Usamos getReference
            // Esto crea un Proxy de Hinernate con el ID.
            // No ejecuta un select en la base de datos.
            // Al no haber un select, no se dispara el flujo de auditoría para el UserModel.
            UserModel userModelProxy = entityManager.getReference(UserModel.class, userPrincipal.id());

            return Optional.of(userModelProxy);

        };
    }

}

