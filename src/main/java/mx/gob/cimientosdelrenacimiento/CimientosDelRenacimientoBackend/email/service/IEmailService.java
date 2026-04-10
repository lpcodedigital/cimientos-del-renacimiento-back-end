package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.email.service;

import java.util.Map;

public interface IEmailService {
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
