package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.dto.MunicipioStadDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.service.MunicipioService;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;

@RestController
@RequestMapping("/api/v1/public/municipios")
@RequiredArgsConstructor
public class PublicMunicipioController {

    private final MunicipioService municipioService;
    private final ObraRespository obraRepository;

    @GetMapping("/stats")
    public List<MunicipioStadDTO> getStats() {

        // Obtener el conteo de obras por municipio desde la base de datos
        List<Object[]> result = obraRepository.countObrasByMunicipalityForPublicTable();

        // 2. Crear el mapa de conteos.
        // TIP: Normalizamos la llave a minúsculas y sin espacios extra para un match
        // más robusto
        Map<String, Long> count = result.stream()
                .collect(
                        Collectors.toMap(
                                res -> ((String) res[0]).trim(),
                                res -> (Long) res[1],
                                (existing, replacement) -> existing // En caso de duplicados, mantener el existente
                        ));

        // 3. Cruzar con la lista maestra de 106 municipios
        // Combinar con la lista de municipios para asegurar que todos estén
        // representados, incluso los que no tienen obras
        return municipioService.getAllMunicipiosList().stream()
                .map(name -> new MunicipioStadDTO(name, count.getOrDefault(name, 0L)))
                .collect(Collectors.toList());
    }
}
