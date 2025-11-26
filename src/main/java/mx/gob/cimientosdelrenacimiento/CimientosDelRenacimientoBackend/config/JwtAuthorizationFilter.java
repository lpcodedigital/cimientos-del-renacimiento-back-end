package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.JwtUtils;
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

        String autHeader = request.getHeader("Authorization");

        if (autHeader == null || !autHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = autHeader.substring(7);

        if(!jwtUtils.validateJwtToken(token)){
            //filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String json = """
            {
                "error": "token invalido o expirado",
                "message": "Tu token es invalido o ha expirado. Por favor, inicia sesion nuevamente."
            }
            """;

            response.getWriter().write(json);
            return;
        }

        String email = jwtUtils.getEmailFromJwtToken(token);

        var user = userRespository.findByEmail(email).orElse(null);

        if(user == null || !user.getActive() ){
            filterChain.doFilter(request, response);
            return; 
        }

        String roleName = user.getRole().getName();

        // Convertir permisos/roles a GrantedAuthorities
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(roleName)
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
