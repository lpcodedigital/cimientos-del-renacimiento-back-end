package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.JwtAccessDeniedHandler;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.JwtAuthEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilitar anotaciones @PreAuthorize y @PostAuthorize
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter, JwtAuthEntryPoint jwtAuthEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
            // Habilitar CORS con la configuración definida en corsConfigurationSource()
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Deshabilitar CSRF por que no vamos a usar formularios y usamos api rest
            .csrf(csrf -> csrf.disable())

            // Configurar la gestion de sesiones como stateless (sin estado)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(exception -> exception
                // Manejar errores de autenticación y autorización
                .authenticationEntryPoint(jwtAuthEntryPoint)

                // Manejar accesos denegados
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            // Configurar las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                
                // Permitir todas las peticiones OPTIONS (Preflight)
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                
                // Permitir acceso sin autenticación a las rutas de autenticación (signup, signin, etc)
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Permitir acceso sin autenticación a las rutas public 
                .requestMatchers("/api/v1/public/**").permitAll()

                // Permitir acceso con un JWT valido a las rutas de usuarios
                .requestMatchers("/api/v1/user/**").authenticated()

                // Permitir acceso con un JWT valido a las rutas de obras
                .requestMatchers("/api/v1/obra/**").authenticated()
                
                // Requerir autenticación para cualquier otra solicitud
                .anyRequest().authenticated()
            )  
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Agregar el filtro de autorización JWT antes del filtro de autenticación por defecto
            .formLogin(form -> form.disable()) // Deshabilitar el formulario de login por defecto
            .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar la autenticación HTTP Basic
        return http.build();
    }

    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Definir los orígenes permitidos (puedes ajustar esto según tus necesidades)
        configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173", // Front Público (Vite Dev)
            "http://localhost:5174", // Front Admin (Refine Dev)
            "http://127.0.0.1:5173", // Front Público (Vite Dev)
            "http://127.0.0.1:5174", // Front Admin (Refine Dev)
            "http://localhost:3000", // Front Público (Docker Prod)
            "http://localhost:3001"  // Front Admin (Docker Prod) 

        ));

        // 2. Definir los metodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. Definir los headers permitidos. Headers que el Front (Refine) necesita enviar y recibir
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "Accept", 
            "X-Requested-With",
            "Cache-Control"
        ));

        // 4. Exponer headers (Si el front necesita leer algún header especial del back)
        //configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        // 5. Permitir credenciales (cookies, tokens, etc)
        configuration.setAllowCredentials(true);

        // 6. Tiempo de vida de la configuración CORS (en segundos)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar esta configuración a todas las rutas
        return source;
    }
    

}
