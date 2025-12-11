package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Maneja errores 401 - Unauthorized (No autenticado)
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException
        ) throws IOException {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String json = "{\"error\": \"Token invalido o expirado\"}";
            response.getWriter().write(json);
        }
}

