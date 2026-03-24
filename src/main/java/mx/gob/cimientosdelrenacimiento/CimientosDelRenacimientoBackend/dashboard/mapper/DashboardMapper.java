package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.dashboard.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    default Map<String, Long> toCountMap(List<Object[]> result) {
        if (result == null) return Collections.emptyMap();

        return result.stream()
            .collect(Collectors.toMap(
                row -> row[0].toString(),
                row -> (Long) row[1]
            ));

    }
}
