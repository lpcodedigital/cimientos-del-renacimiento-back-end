package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF por que no vamos a usar formularios y usamos api rest
            .csrf(csrf -> csrf.disable())
            // Configurar las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                
                // Permitir acceso sin autenticación a las rutas de autenticación (signup, signin, etc)
                .requestMatchers("/api/v1/auth/**").permitAll()

                .requestMatchers("/api/v1/roles/**").permitAll()
                .requestMatchers("/api/v1/user/**").permitAll()
                
                // Permitir acceso con autenticación a cualquier otra ruta
                //.anyRequest().authenticated()
                
                // Permitir acceso sin autenticación a cualquier otra ruta
                .anyRequest().permitAll()
            )   
            .formLogin(form -> form.disable()) // Deshabilitar el formulario de login por defecto
            .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar la autenticación HTTP Basic
        return http.build();
    }

}
