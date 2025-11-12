package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.model.PermissionModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.repository.PermissionRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.repository.RoleRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRespository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0){
            seedDataBae();
        }
    }

    private void seedDataBae(){

        System.out.println("🚧 Inicializando datos base...");

        // Crear permisos basicos
        PermissionModel create = permissionRepository.save(PermissionModel.builder()
            .name("CREATE")
            .description("Puede crear registros")
            .build()
        );
        PermissionModel read = permissionRepository.save(PermissionModel.builder()
            .name("READ")
            .description("Puede leer registros")
            .build()
        );    
        PermissionModel update = permissionRepository.save(PermissionModel.builder()
            .name("UPDATE")
            .description("Puede actualizar registros")
            .build()
        );
        PermissionModel delete = permissionRepository.save(PermissionModel.builder()
            .name("DELETE")
            .description("Puede eliminar registros")
            .build()

        );
        // Roles basicos
        RoleModel adminRole = roleRepository.save(RoleModel.builder()
            .name("ROLE_ADMIN")
            .description("Rol de administrador con todos los permisos")
            .permissions(Set.of(create, read, update, delete))
            .build()
        );
        RoleModel userRole = roleRepository.save(RoleModel.builder()
            .name("ROLE_USER")
            .description("Rol de usuario con permisos limitados")
            .permissions(Set.of(create, read, update))
            .build()
        );
        RoleModel guestRole = roleRepository.save(RoleModel.builder()
            .name("ROLE_GUEST")
            .description("Rol de invitado con permisos muy limitados")
            .permissions(Set.of(read))
            .build()
        );

        // Crear un usuarios por defecto
        userRepository.save(UserModel.builder()
            .name("Admin")
            .middleName("Default")
            .firstLastName("Default")
            .secondLastName("User")
            .phone("5555555555")
            .email("admin@cimientosdelrenacimiento.gob.mx")
            .password(passwordEncoder.encodePassword("Admin_2025"))
            .isFirstLogin(false)
            .twoFactorEnabled(false)
            .twoFactorSecret(null)
            .verificationCode(null)
            .role(adminRole)
            .build());

         userRepository.save(UserModel.builder()
            .name("User")
            .middleName("Default")
            .firstLastName("Default")
            .secondLastName("User")
            .phone("5555555551")
            .email("user@cimientosdelrenacimiento.gob.mx")
            .password(passwordEncoder.encodePassword("User_2025"))
            .isFirstLogin(false)
            .twoFactorEnabled(false)
            .twoFactorSecret(null)
            .verificationCode(null)
            .role(userRole)
            .build());

        userRepository.save(UserModel.builder()
            .name("Guest")
            .middleName("Default")
            .firstLastName("Default")
            .secondLastName("Guest")
            .phone("5555555552")
            .email("guest@cimientosdelrenacimiento.gob.mx")
            .password(passwordEncoder.encodePassword("Guest_2025"))
            .isFirstLogin(false)
            .twoFactorEnabled(false)
            .twoFactorSecret(null)
            .verificationCode(null)
            .role(guestRole)
            .build());

            
        System.out.println("✅ Datos iniciales creados correctamente.");
    }


}
