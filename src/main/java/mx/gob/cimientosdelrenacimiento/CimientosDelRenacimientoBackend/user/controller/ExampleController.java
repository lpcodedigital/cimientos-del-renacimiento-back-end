package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    
    @GetMapping("/")
    public String  example(){
        return "Spirng boot is running mode dev...";
    }
}
