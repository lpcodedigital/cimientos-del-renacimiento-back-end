package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.CursoRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto.DashboardStatsDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.mapper.DashboardMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.StringNormalizer;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final DashboardMapper dashboardMapper;
    private final ObraRespository obraRespository;
    private final UserRespository userRepository;
    private final CursoRepository cursoRepository;


    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getStats() {

        List<Object[]> mainRaw = obraRespository.countObrasByMunicipality();

        // Normalizamos y agrupamos manualmente los municipios
        Map<String, Long> normalizedMuni = mainRaw.stream()
        .collect(Collectors.groupingBy(
            row -> StringNormalizer.normalizer(row[0].toString()),
            Collectors.summingLong(row -> (Long) row[1])
        ));

        return DashboardStatsDTO.builder()
            .totalObras(obraRespository.count())
            .totalInvestment(obraRespository.sumAllInvestment())
            .averageProgress(obraRespository.getAverageProgress() != null ? obraRespository.getAverageProgress() : 0.0)
            .countByStatus(dashboardMapper.toCountMap(obraRespository.countObrasByStatus()))
            .countByMunicipality(normalizedMuni)
            .totalUsers(userRepository.countTotalUsers())
            .activeUsers(userRepository.countActiveUsers())
            .totalCursos(cursoRepository.countActiveCourses())
            .municipalitiesWithObras(obraRespository.countDistinctMunicipalitiesWithObras())
            .countByAgency(dashboardMapper.toCountMap(obraRespository.countObrasByAgency()))
            .totalAgency(obraRespository.countDistinctAgencies())
            .build();
    }

}
