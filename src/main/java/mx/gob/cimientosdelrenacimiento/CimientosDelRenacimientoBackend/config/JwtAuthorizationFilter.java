package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
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
    public JwtAuthorizationFilter(JwtUtils jwtUtils, UserRespository userRespository) {
        this.jwtUtils = jwtUtils;
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

        // Token invalido o expirado
        if (!jwtUtils.validateJwtToken(token)) {
            throw new BadCredentialsException("Token inválido o expirado");
        }
        
        //if(!jwtUtils.validateJwtToken(token)){
        //    //throw new BadCredentialsException("Token inválido o expirado");
        //    filterChain.doFilter(request, response);  // <— NO SE LANZA EXCEPCIÓN
        //    return;
        //}

        String email = jwtUtils.getEmailFromJwtToken(token);

        if (email == null){
            throw new BadCredentialsException("Token invalido");
        }

        String role = jwtUtils.getRoleFromJwtToken(token);

        //UserModel user = userRespository.findByEmail(email).orElse(null);

        //if (user == null || user.isDeleted()) {
        //    filterChain.doFilter(request, response);
        //    return;
        //}

        // Convertir permisos/roles a GrantedAuthorities
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(role)
        );

        //System.out.println("Authorities del usuario: " + authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
