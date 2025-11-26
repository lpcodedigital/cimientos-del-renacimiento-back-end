package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model;

import org.hibernate.annotations.SQLRestriction;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.common.Auditable;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;

@Entity
@Data
@Table(name = "users")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class UserModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String middleName;

    @Column(nullable = false)
    private String firstLastName;

    @Column(nullable = false)
    private String secondLastName;

    @Column(nullable = true, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isFirstLogin = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(nullable = true)
    private String twoFactorSecret;

    @Column(nullable = true)
    private Integer verificationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "idRole", nullable = false)
    @JsonManagedReference //Evita cliclos al listar usuarios con roles
    private RoleModel role;

}
