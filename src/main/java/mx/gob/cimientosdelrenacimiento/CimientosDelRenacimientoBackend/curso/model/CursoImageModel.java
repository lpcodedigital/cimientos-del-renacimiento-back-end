package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model;


import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.*;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.common.Auditable;

@Entity
@Data
@Table(name = "curso_images")
@SQLDelete(sql = "UPDATE curso_images SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) 
public class CursoImageModel extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    private String providerId;

    private String thumbUrl;

    private String mimeType;

    private String size;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude 
    @EqualsAndHashCode.Exclude 
    private CursoModel curso;
}
