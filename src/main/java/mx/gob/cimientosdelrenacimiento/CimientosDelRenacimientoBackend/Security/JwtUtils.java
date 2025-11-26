package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.Security;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long jwtExpirationMs = Duration.ofHours(4).toMillis();

    public JwtUtils(@Value("${JWT_SECRET}") String base64Key){
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(String email, String role, Date expiration) {
        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(expiration)
            .signWith(secretKey)
            .compact();
    }

    public Boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + authToken);
            return false;
        }
    }

    public Date generateExpirationDate(){
        return new Date(System.currentTimeMillis() + jwtExpirationMs);
    }

    public String getEmailFromJwtToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getRoleFromJwtToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }
}
