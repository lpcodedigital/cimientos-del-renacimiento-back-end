package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringNormalizer {
    public static String normalizer(String input){
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

}
