package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateFormatter {

    public String formatDateTime(LocalDateTime dateTime) {

        final ZoneId ZONE_ID = ZoneId.of("America/Mexico_City");
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZONE_ID);
        return zonedDateTime.format(dateTimeFormater);
    }

}
