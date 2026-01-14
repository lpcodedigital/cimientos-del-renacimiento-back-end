package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.common.Auditable;

@Entity
@Data
@Table(name = "obras")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class ObraModel extends Auditable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String municipality;

    @Column(nullable = false)
    private String agency;
    
    private BigDecimal investment;

    private Integer progress;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoObraEnum status;

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // <--- EVITA QUE EL TOSTRING DE OBRA LLAME AL DE LAS IMÁGENES
    private List<ObraImageModel> images = new ArrayList<>();

    // Método helper para sincronizar la relación bidireccional
    public void addImage(ObraImageModel image){

        if(this.images.size() >= 10){
            throw new IllegalStateException("No se pueden agregar más de 10 imágenes a una obra.");
        }

        images.add(image); // Agrega la imagen a la lista de imágenes de la obra
        image.setObra(this); // Le dice a la imagen : "Yo (this) soy tu obra"
    }

}
