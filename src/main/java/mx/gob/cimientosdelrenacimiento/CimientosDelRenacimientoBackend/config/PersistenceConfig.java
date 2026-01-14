package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {

    private final ApplicationContext context;

    // Inyectamos el ApplicationContext en lugar del repositorio directamente
    public PersistenceConfig(ApplicationContext context) {
        this.context = context;
    }

    //@Bean
    //public AuditorAware<UserModel> auditorProvider() {
    //    return () -> {
    //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //    
    //        if (authentication == null || !authentication.isAuthenticated() || 
    //            authentication instanceof AnonymousAuthenticationToken) {
    //            return Optional.empty();
    //        }
    //    
    //        // Si tu JwtAuthorizationFilter guarda al usuario como objeto:
    //        if (authentication.getPrincipal() instanceof UserModel) {
    //            return Optional.of((UserModel) authentication.getPrincipal());
    //        }
    //    
    //        // Si lo guarda como String (email), lo buscamos una sola vez de forma perezosa
    //        String email = (String) authentication.getPrincipal();
    //        return context.getBean(UserRespository.class).findByEmail(email);
    //        //try {
    //        //    return context.getBean(UserRespository.class).findByEmail(email);
    //        //} catch (Exception e) {
    //        //    return Optional.empty(); // Evita que el error rompa la petición
    //        //}
    //    };
    //}

    @Bean
    public AuditorAware<UserModel> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty();
            }
        
            return Optional.empty(); 
        };
    }

}

