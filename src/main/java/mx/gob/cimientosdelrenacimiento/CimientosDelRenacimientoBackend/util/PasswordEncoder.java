package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

   private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

   public String encodePassword(String rawPassword){
         return encoder.encode(rawPassword);
   }

   public Boolean matches(String rawPassword, String encodedPassword){
         return encoder.matches(rawPassword, encodedPassword);
   }

}
