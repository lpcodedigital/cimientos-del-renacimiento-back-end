package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilitar anotaciones @PreAuthorize y @PostAuthorize
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF por que no vamos a usar formularios y usamos api rest
            .csrf(csrf -> csrf.disable())

            // Configurar la gestion de sesiones como stateless (sin estado)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configurar las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                
                // Permitir acceso sin autenticación a las rutas de autenticación (signup, signin, etc)
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Permitir acceso con un JWT valido a las rutas de usuarios
                .requestMatchers("/api/v1/user/**").authenticated()
                
                // Requerir autenticación para cualquier otra solicitud
                .anyRequest().authenticated()
            )   
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Agregar el filtro de autorización JWT antes del filtro de autenticación por defecto
            .formLogin(form -> form.disable()) // Deshabilitar el formulario de login por defecto
            .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar la autenticación HTTP Basic
        return http.build();
    }

}
