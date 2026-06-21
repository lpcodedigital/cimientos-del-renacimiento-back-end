package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraLinkProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraMapaProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraPaginationProjection;

public interface ObraRespository extends JpaRepository<ObraModel, Long> {

        /*** QUERIES PARA USAR EN EL MODULO DE OBRAS DEL FRONTEND ADMIN ***/

        // Verificar si existe una obra con el nombre dado, incluyendo las eliminadas
        // por soft delete
        @Query(value = "SELECT * FROM obras WHERE name = ?1", nativeQuery = true)
        Optional<ObraModel> findAnyByNombre(String name);

        // Consulta para optimizar el mapa
        @Query("SELECT o.id as id, o.name as name, o.latitude as latitude, " +
                        "o.longitude as longitude, o.municipality as municipality " +
                        "FROM ObraModel o")
        List<ObraMapaProjection> findAllForMap();

        // Consulta para el detalle: Carga optimizada de imagnes (evitando N+1)
        @Query("SELECT o FROM ObraModel o LEFT JOIN FETCH o.images WHERE o.id = ?1")
        Optional<ObraModel> findByIdWithImages(Long id);

        // Consulta para la paginación usando la proyección personalizada
        @Query("SELECT o.id as id, o.name as name, o.municipality as municipality, o.description as description, o.status as status, o.progress as progress, o.createdAt as createdAt"
                        +
                        " FROM ObraModel o WHERE o.deleted = false")
        Page<ObraPaginationProjection> findAllPaginated(Pageable pageable);

        // Consulta para búsqueda de texto completo usando la columna search_vector
        @Query(value = "SELECT o.id as id, o.name as name, o.municipality as municipality, " +
               "o.description as description, o.status as status, o.progress as progress, o.created_at as createdAt " +
               "FROM obras o " +
               "WHERE o.deleted = false " +
               "AND (:search IS NULL OR o.search_vector @@ to_tsquery('es_unaccent', :search)) " +
               "ORDER BY o.id ASC", 
       countQuery = "SELECT COUNT(*) FROM obras o WHERE o.deleted = false AND (:search IS NULL OR o.search_vector @@ to_tsquery('es_unaccent', :search))",
       nativeQuery = true)
        Page<ObraPaginationProjection> findAllByFullTextSearch(@Param("search") String search, Pageable pageable);
        /*** QUERIES PARA USAR EN EL MODULO DEL DASHBOARD DEL FRONTEND PUBLIC ***/

        // TotaL invertido en obras
        @Query("SELECT SUM(o.investment) FROM ObraModel o WHERE o.deleted = false")
        BigDecimal sumAllInvestment();

        // Progreso promedio de obras
        @Query("SELECT AVG(o.progress) FROM ObraModel o WHERE o.deleted = false")
        Double getAverageProgress();

        // Conteo de obras por estatus
        @Query("SELECT o.status, COUNT(o) FROM ObraModel o WHERE o.deleted = false GROUP BY o.status")
        List<Object[]> countObrasByStatus();

        // Conteo de obras por municipio
        @Query("SELECT UPPER(o.municipality), COUNT(o) FROM ObraModel o WHERE o.deleted = false GROUP BY UPPER(o.municipality)")
        List<Object[]> countObrasByMunicipality();

        /*** QUERYS PARA USAR EN FRONTEND PUBLICO ***/

        /** 
         * Query para la Tabla Pública de Municipios.
         * Agrupa respetando el formato original del nombre para el match con el
         * GeoJSON.
         */
        @Query("SELECT o.municipality, COUNT(o) FROM ObraModel o WHERE o.deleted = false GROUP BY o.municipality")
        List<Object[]> countObrasByMunicipalityForPublicTable();

        // Consulta para obtener solo id y nombre de obras por municipio, optimizada
        // para la tabla pública
        @Query("SELECT o.id as id, o.name as name FROM ObraModel o WHERE o.deleted = false AND o.municipality = :municipio")
        List<ObraLinkProjection> findObrasByMunicipalityLight(@Param("municipio") String municipio);

        // Contar cuántos municipios distintos tienen obras (cobertura)
        @Query("SELECT COUNT(DISTINCT o.municipality) FROM ObraModel o WHERE o.deleted = false")
        long countDistinctMunicipalitiesWithObras();

        // Conteo de obras agrupado por dependencia ejecutora (para la nueva gráfica)
        @Query("SELECT o.agency, COUNT(o) FROM ObraModel o WHERE o.deleted = false GROUP BY o.agency")
        List<Object[]> countObrasByAgency();

        // Conteo de ejecutoras distintas
        @Query("SELECT COUNT(DISTINCT o.agency) FROM ObraModel o WHERE o.deleted = false")
        long countDistinctAgencies();
}
