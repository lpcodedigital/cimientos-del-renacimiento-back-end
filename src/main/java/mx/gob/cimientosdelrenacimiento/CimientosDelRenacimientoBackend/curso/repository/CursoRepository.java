package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.projections.CursoPaginationProjection;

@Repository
public interface CursoRepository extends JpaRepository<CursoModel, Long> {

    /**
     * Consulta Paginada para el Administrador (Refine).
     * Trae la entidad completa pero optimiza la consulta inicial.
     * Usamos esto para que Refine pueda manejar el componente <List />.
     */
    @Query("SELECT c FROM CursoModel c JOIN FETCH c.municipality WHERE c.deleted = false")
    Page<CursoModel> findAllAdminPaginated(Pageable pageable);

    /**
     * Consulta Paginada para el Front Público.
     * Seleccionamos los campos básicos y la URL de la imagen de portada.
     * Hacemos un LEFT JOIN con coverImage para traer la URL en una sola consulta.
     */
    @Query("SELECT c.id as id, c.title as title, m.name as municipalityName, " +
           "c.description as description, c.courseDate as courseDate, " +
           "img.url as coverImageUrl " +
           "FROM CursoModel c " +
           "JOIN c.municipality m " +
           "LEFT JOIN c.coverImage img " +
           "WHERE c.deleted = false")
    Page<CursoPaginationProjection> findAllPublicPaginated(Pageable pageable);
    
    /**
     * Consulta para el Detalle del Curso.
     * Usamos FETCH para cargar la galería completa de imágenes en una sola consulta
     * y evitar que Hibernate haga una consulta extra por cada imagen.
     */
    // Consulta para el detalle: Carga optimizada de imagnes (evitando N+1)
    @Query("SELECT c FROM CursoModel c " +
           "LEFT JOIN FETCH c.images " +
           "LEFT JOIN FETCH c.coverImage " +
           "JOIN FETCH c.municipality " +
           "WHERE c.id = ?1 AND c.deleted = false")
    Optional<CursoModel> findByIdWithImages(Long id);

    // Consulta para la paginación usando la proyección personalizada
    @Query("SELECT c.id as id, c.title as title, c.description as description, m.name as municipalityName, c.courseDate as courseDate, ci.url as coverImageUrl " +
           "FROM CursoModel c JOIN c.municipality m LEFT JOIN c.coverImage ci")
    Page<CursoPaginationProjection> findAllProjected(Pageable pageable);

    /**
     * Conteo para el Dashboard (para mostrar stats de capacitación)
     */
    @Query("SELECT COUNT(c) FROM CursoModel c WHERE c.deleted = false")
    long countActiveCourses();

}
