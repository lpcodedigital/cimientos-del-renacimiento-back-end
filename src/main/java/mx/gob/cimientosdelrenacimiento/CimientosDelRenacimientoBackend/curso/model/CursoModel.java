package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model;

import java.time.LocalDate;
import java.util.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.*;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.common.Auditable;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model.MunicipioModel;

@Entity
@Table(name = "cursos")
@Data
@SQLDelete(sql = "UPDATE cursos SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class CursoModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = true)
    private MunicipioModel municipality;

    @Column(nullable = true)
    private LocalDate courseDate;

    // Relación para la galería de imágenes
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CursoImageModel> images = new ArrayList<>();

    // Relación opcional para identificar específicamente la portada
    // Esto facilita mucho las consultas para el front público
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_image_id", referencedColumnName = "id")
    private CursoImageModel coverImage;

    // Método helper para sincronizar la relación bidireccional
    public void addImage(CursoImageModel image) {
        if (this.images.size() >= 11) { // 1 portada + 10 galería
            throw new IllegalStateException("No se pueden agregar más de 10 imágenes a un curso.");
        }
        images.add(image); // Agrega la imagen a la lista de imágenes del curso
        image.setCurso(this); // Le dice a la imagen : "Yo (this) soy tu curso"
    }

}
