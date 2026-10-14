package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.repository.AppMobileRepository;

@Service
@RequiredArgsConstructor
public class AppMobileServiceImpl implements IAppMobileService {

   private final AppMobileRepository globalSearchRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AppMobileGlobalSearchCardDTO> search(String query, int page, int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        String tsQuery = formatToTsQuery(query);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tsQuery", tsQuery);
        params.addValue("limit", size);
        params.addValue("offset", pageRequest.getOffset());

        Long total = globalSearchRepository.countTotalResults(params);

        if (total == null || total == 0) {
            return new PageImpl<>(List.of(), pageRequest, 0);
        }

        List<AppMobileGlobalSearchCardDTO> results = globalSearchRepository.findPaginatedResults(params);

        return new PageImpl<>(results, pageRequest, total);
    }

    private String formatToTsQuery(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }
        return Arrays.stream(search.trim().split("\\s+"))
                .map(term -> term + ":*")
                .collect(Collectors.joining(" & "));
    }

}
