package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "municipios")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MunicipioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String cveGeo;

    @Column(nullable = false)
    private String cveEnt;

    @Column(nullable = false, unique = true)
    private String cveMun;

    @Column(nullable = false)
    private String cveCab;

    @Column(nullable = true)
    private String pobTotal;

    @Column(nullable = true)
    private String pobFemenina;

    @Column(nullable = true)
    private String pobMasculina;

    @Column(nullable = true)
    private String totalViviendas;

}
