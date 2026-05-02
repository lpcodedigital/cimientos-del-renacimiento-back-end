package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalObras;
    private BigDecimal totalInvestment;
    private double averageProgress;

    // Estos Maps contienen el "nombre del grupo" -> conteo
    private Map<String, Long> countByStatus; // Función: Conteo para gráfica de pastel/estatus
    private Map<String, Long> countByMunicipality; // Función: Conteo para gráfica de barras/geografíco
    
    private long municipalitiesWithObras; // KPI de Cobertura
    private Map<String, Long> countByAgency; // KPI de Ejecutoras

    // Usuarios
    private long totalUsers;
    private long activeUsers;

    // Cursos
    private long totalCursos;

    // Ejecutoras
    private long totalAgency;
}
