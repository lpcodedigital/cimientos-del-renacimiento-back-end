package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileGlobalSearchCardDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.app_mobile.dto.AppMobileLightResponseDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.EstadoObraEnum;

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

    public List<AppMobileLightResponseDTO> findMapResultsByMunicipio(MapSqlParameterSource params) {
    String dataSql = """
                SELECT * FROM (
                    -- BLOQUE OBRAS
                    SELECT 
                        'OBRA' as tipo,
                        o.id as id,
                        o.name as name,
                        o.description as description,
                        o.latitude as latitude,
                        o.longitude as longitude,
                        o.municipality as municipality,
                        o.locality as locality,
                        o.agency as agency,
                        o.investment as investment,
                        o.status as status,
                        o.progress as progress,
                        o.created_at as createdAt,
                        o.updated_at as updatedAt,
                        o.deleted_at as deletedAt
                    FROM obras o
                    WHERE o.deleted = false
                      -- Se normaliza la columna y el parámetro a minúsculas y sin acentos
                      AND unaccent(lower(o.municipality)) = unaccent(lower(:municipality))

                    UNION ALL

                    -- BLOQUE CURSOS
                    SELECT 
                        'CURSO' as tipo,
                        c.id as id,
                        c.title as name,
                        c.description as description,
                        c.latitude as latitude,
                        c.longitude as longitude,
                        m.name as municipality,
                        NULL as locality,
                        NULL as agency,
                        NULL as investment,
                        NULL as status,
                        NULL as progress,
                        c.created_at as createdAt,
                        c.updated_at as updatedAt,
                        c.deleted_at as deletedAt
                    FROM cursos c
                    LEFT JOIN municipios m ON c.municipio_id = m.id
                    WHERE c.deleted = false
                      -- Se normaliza la columna y el parámetro a minúsculas y sin acentos
                      AND unaccent(lower(m.name)) = unaccent(lower(:municipality))
                ) as map_search
                ORDER BY createdAt DESC
            """;

    return jdbcTemplate.query(dataSql, params, (rs, rowNum) -> new AppMobileLightResponseDTO(
            rs.getString("tipo"),
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            rs.getString("municipality"),
            rs.getString("locality"),
            rs.getString("agency"),
            rs.getBigDecimal("investment"),
            // Convierte el String del SQL al Enum de forma segura si no es nulo
            rs.getString("status") != null ? EstadoObraEnum.valueOf(rs.getString("status")) : null,
            rs.getObject("progress") != null ? rs.getInt("progress") : null,
            // Conversión de Timestamp a LocalDateTime
            rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
            rs.getTimestamp("updatedAt") != null ? rs.getTimestamp("updatedAt").toLocalDateTime() : null,
            rs.getTimestamp("deletedAt") != null ? rs.getTimestamp("deletedAt").toLocalDateTime() : null
    ));
}
}
