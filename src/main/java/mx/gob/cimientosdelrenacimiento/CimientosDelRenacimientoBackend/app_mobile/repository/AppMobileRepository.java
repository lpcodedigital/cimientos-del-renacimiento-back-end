package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;

@Repository
@RequiredArgsConstructor
public class AppMobileRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Long countTotalResults(MapSqlParameterSource params) {
        String countSql = """
                    SELECT COUNT(*) FROM (
                        SELECT o.id FROM obras o
                        WHERE o.deleted = false AND (:tsQuery = '' OR o.search_vector @@ to_tsquery('es_unaccent', :tsQuery))
                        UNION ALL
                        SELECT c.id FROM cursos c
                        WHERE c.deleted = false AND (:tsQuery = '' OR c.search_vector @@ to_tsquery('es_unaccent', :tsQuery))
                    ) as total_results
                """;
        return jdbcTemplate.queryForObject(countSql, params, Long.class);
    }

    public List<AppMobileGlobalSearchCardDTO> findPaginatedResults(MapSqlParameterSource params) {
        String dataSql = """
                    SELECT * FROM (
                        -- BLOQUE OBRAS: Portada Implícita (Toma la primera imagen subida a la galería)
                        SELECT
                            'OBRA' as tipo,
                            o.id as idOriginal,
                            o.name as titulo,
                            o.municipality as municipio,
                            o.locality as localidad,
                            o.description as descripcionCorta,
                            o.status as estatus,
                            o.progress as progreso,
                            (SELECT img.url FROM obra_images img WHERE img.obra_id = o.id AND img.deleted = false ORDER BY img.id ASC LIMIT 1) as imagenUrl,
                            o.created_at as createdAt
                        FROM obras o
                        WHERE o.deleted = false
                          AND (:tsQuery = '' OR o.search_vector @@ to_tsquery('es_unaccent', :tsQuery))

                        UNION ALL

                        -- BLOQUE CURSOS: Portada Explícita (Toma la imagen asignada en cover_image_id)
                        SELECT
                            'CURSO' as tipo,
                            c.id as idOriginal,
                            c.title as titulo,
                            m.name as municipio,
                            NULL as localidad,
                            c.description as descripcionCorta,
                            NULL as estatus,
                            NULL as progreso,
                            img.url as imagenUrl,
                            c.created_at as createdAt
                        FROM cursos c
                        LEFT JOIN municipios m ON c.municipio_id = m.id
                        LEFT JOIN curso_images img ON c.cover_image_id = img.id
                        WHERE c.deleted = false
                          AND (:tsQuery = '' OR c.search_vector @@ to_tsquery('es_unaccent', :tsQuery))
                    ) as global_search
                    ORDER BY createdAt DESC
                    LIMIT :limit OFFSET :offset
                """;

        return jdbcTemplate.query(dataSql, params, (rs, rowNum) -> AppMobileGlobalSearchCardDTO.builder()
                .tipo(rs.getString("tipo"))
                .idOriginal(rs.getLong("idOriginal"))
                .titulo(rs.getString("titulo"))
                .municipio(rs.getString("municipio"))
                .localidad(rs.getString("localidad"))
                .descripcionCorta(rs.getString("descripcionCorta"))
                .estatus(rs.getString("estatus"))
                .progreso(rs.getObject("progreso") != null ? rs.getInt("progreso") : null)
                .imagenUrl(rs.getString("imagenUrl"))
                .build());
    }

}
