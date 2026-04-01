package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {

    private final ObjectMapper objectMapper;
    private List<String> listaNombreMunicipios = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            // ruta del archivo Geojson en src/main/resources/
            ClassPathResource resource = new ClassPathResource("yucatan_municipios_2023.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            
            JsonNode features = root.path("features");
            for (JsonNode feature : features) {
                String nombre = feature.path("properties").path("NOMGEO").asText();
                listaNombreMunicipios.add(nombre);
            }
            // Ordenamos alfabéticamente para la tabla
            listaNombreMunicipios = listaNombreMunicipios.stream().sorted().collect(Collectors.toList());
            System.out.println("✅ Catálogo de " + listaNombreMunicipios.size() + " municipios cargado en memoria.");
        } catch (IOException e) {
            System.err.println("❌ No se pudo cargar el GeoJSON de municipios: " + e.getMessage());
        }
    }

    public List<String> getAllMunicipiosList() {
        return listaNombreMunicipios;
    }


}
