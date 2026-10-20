package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileCursoDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileObraDetailDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.mapper.AppMobileMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.repository.AppMobileRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.CursoRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;

@Service
@RequiredArgsConstructor
public class AppMobileServiceImpl implements IAppMobileService {

   private final AppMobileRepository globalSearchRepository;
   private final ObraRespository obraRepository;
   private final CursoRepository cursoRepository;
   private final AppMobileMapper appMobileMapper;

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

    @Override
    @Transactional(readOnly = true)
    public AppMobileObraDetailDTO getObraDetail(Long obraId) {
        ObraModel obra = obraRepository.findById(obraId)
                .orElseThrow(() -> new RuntimeException("Obra not found with id: " + obraId));
       
        return appMobileMapper.toAppMobileObraDetailDTO(obra);
    }

    @Override
    @Transactional(readOnly = true)
    public AppMobileCursoDetailDTO getCursoDetail(Long cursoId) {
        CursoModel curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso not found with id: " + cursoId));

        return appMobileMapper.toAppMobileCursoDetailDTO(curso);
    }

}
