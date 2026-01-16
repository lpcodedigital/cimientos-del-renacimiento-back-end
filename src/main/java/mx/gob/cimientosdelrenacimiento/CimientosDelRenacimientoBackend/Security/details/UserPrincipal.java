package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security.details;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Representa la identidad del usuario autenticado en el SecurityContext.
 * Evita cargar la entidad completa UserModel de la BD durante la auditoría.
 */ 
public record UserPrincipal(
    
    Long id,
    String email,
    Collection<? extends GrantedAuthority> authorities

) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        return authorities;
    }

    @Override
    public String getPassword() {
        
       return null; // El token es nuestra "Contraseña" ya validada
    }

    @Override
    public String getUsername() {
        
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}