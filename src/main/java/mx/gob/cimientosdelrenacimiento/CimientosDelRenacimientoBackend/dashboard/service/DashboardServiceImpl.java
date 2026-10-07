package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.CursoRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto.DashboardStatsDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto.MunicipioResumenDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.mapper.DashboardMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.StringNormalizer;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final DashboardMapper dashboardMapper;
    private final ObraRespository obraRepository;
    private final UserRespository userRepository;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getStats() {

        List<Object[]> mainRaw = obraRepository.countObrasByMunicipality();

        // Normalizamos y agrupamos manualmente los municipios
        Map<String, Long> normalizedMuni = mainRaw.stream()
                .collect(Collectors.groupingBy(
                        row -> StringNormalizer.normalizer(row[0].toString()),
                        Collectors.summingLong(row -> (Long) row[1])));

        return DashboardStatsDTO.builder()
                .totalObras(obraRepository.count())
                .totalInvestment(obraRepository.sumAllInvestment())
                .averageProgress(
                        obraRepository.getAverageProgress() != null ? obraRepository.getAverageProgress() : 0.0)
                .countByStatus(dashboardMapper.toCountMap(obraRepository.countObrasByStatus()))
                .countByMunicipality(normalizedMuni)
                .totalUsers(userRepository.countTotalUsers())
                .activeUsers(userRepository.countActiveUsers())
                .totalCursos(cursoRepository.countActiveCourses())
                .municipalitiesWithObras(obraRepository.countDistinctMunicipalitiesWithObras())
                .countByAgency(dashboardMapper.toCountMap(obraRepository.countObrasByAgency()))
                .totalAgency(obraRepository.countDistinctAgencies())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipioResumenDTO> getResumenGlobalPorMunicipioApp() {
        Map<String, MunicipioResumenDTO> mapResultados = new HashMap<>();

        // 1. Procesar Obras
        var obrasStats = obraRepository.getResumenObrasPorMunicipio();
        for (var stat : obrasStats) {
            // Normalizar el nombre a mayúsculas o como prefieras para evitar desajustes
            String nombreMunicipio = stat.getMunicipality().trim().toUpperCase();
            
            MunicipioResumenDTO dto = new MunicipioResumenDTO();
            dto.setMunicipio(nombreMunicipio);
            dto.setTotalObras(stat.getTotalObras());
            dto.setTotalInversion(stat.getTotalInversion() != null ? stat.getTotalInversion() : java.math.BigDecimal.ZERO);
            dto.setObrasFinalizadas(stat.getFinalizadas());
            dto.setObrasEnProceso(stat.getEnProceso());
            dto.setLatitude(stat.getLatitude());
            dto.setLongitude(stat.getLongitude());
            
            mapResultados.put(nombreMunicipio, dto);
        }

        // 2. Procesar Cursos y Mezclar
        var cursosStats = cursoRepository.getResumenCursosPorMunicipio();
        for (var stat : cursosStats) {
            String nombreMunicipio = stat.getMunicipality().trim().toUpperCase();
            
            // Si el municipio ya existe por tener obras, lo recuperamos; si no, lo creamos.
            MunicipioResumenDTO dto = mapResultados.getOrDefault(nombreMunicipio, new MunicipioResumenDTO());
            
            dto.setMunicipio(nombreMunicipio);
            dto.setTotalCursos(stat.getTotalCursos());
            
            // Si el municipio solo tiene cursos (o si la obra no tenía coordenadas registradas), asignamos las del curso
            if (dto.getLatitude() == null) {
                dto.setLatitude(stat.getLatitude());
                dto.setLongitude(stat.getLongitude());
            }
            
            mapResultados.put(nombreMunicipio, dto);
        }

        // 3. Devolver la lista final de valores
        return mapResultados.values().stream().collect(Collectors.toList());
    }

}
