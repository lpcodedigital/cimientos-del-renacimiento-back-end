package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.common.Auditable;

@Entity
@Data
@Table(name = "obra_images")
@SQLRestriction("deleted = false")   
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Importante para clases que heredan de otras
public class ObraImageModel extends Auditable {

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
    @JoinColumn(name = "obra_id", nullable = false)
    @ToString.Exclude // <--- EVITA QUE EL TOSTRING DE OBRAIMAGE LLAME AL DE OBRA, ROMPE EL BLUCLE TOSTRING
    @EqualsAndHashCode.Exclude // <--- EVITA QUE EL EQUALS DE OBRAIMAGE LLAME AL DE OBRA, ROMPE EL BLUCLE EQUALS
    private ObraModel obra;

}
