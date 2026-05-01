package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.JwtUtils;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.details.UserPrincipal;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserRespository userRespository;


    public JwtAuthorizationFilter(JwtUtils jwtUtils, UserRespository userRespository) {
        this.jwtUtils = jwtUtils;
        this.userRespository = userRespository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Permitir solicitudes OPTIONS sin autenticación (Preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String autHeader = request.getHeader("Authorization");

        if (autHeader == null || !autHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = autHeader.substring(7);

        // 1- Validar el token JWT
        if (!jwtUtils.validateJwtToken(token)) {
            // throw new BadCredentialsException("Token inválido o expirado");
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail = jwtUtils.getEmailFromJwtToken(token);

        // BUSCAR AL USUARIO EN LA BD (O en un Cache como Redis)
        var user = userRespository.findByEmail(userEmail).orElse(null);

        if (user == null || !user.getActive()) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Usuario inactivo o no encontrado");
            return;
        }

        try {
            // 2. Extraer Claims (Obtenemos tod de una vez)
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);
            Boolean isActive = claims.get("active", Boolean.class);

            if (isActive == null || !isActive) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cuenta desactivada");
                return;
            }

            if (email == null || userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Crear el UserPrincipal (nuevo objeto ligero)
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            UserPrincipal userPrincipal = new UserPrincipal(
                    userId,
                    email,
                    authorities);

            // 4. Establecer la autenticación
            // passamos el userPrincipal como principal en lugar del email
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Si algo falla en el parseo (ej. UUID inválido), limpiamos el contexto
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
