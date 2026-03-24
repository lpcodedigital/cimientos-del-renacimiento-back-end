package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraMapaProjection;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.projections.ObraPaginationProjection;

public interface ObraRespository extends JpaRepository<ObraModel, Long> {

    // Verificar si existe una obra con el nombre dado, incluyendo las eliminadas por soft delete
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
    @Query("SELECT o.id as id, o.name as name, o.municipality as municipality, o.description as description, o.status as status, o.progress as progress, o.createdAt as createdAt" + 
    " FROM ObraModel o WHERE o.deleted = false")
    Page<ObraPaginationProjection> findAllPaginated(Pageable pageable); // Spring Data JPA generará la consulta automáticamente

    /*** Querys para usar en el Dashboard ***/

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

    /*** Querys para usar en el Dashboard ***/
}
