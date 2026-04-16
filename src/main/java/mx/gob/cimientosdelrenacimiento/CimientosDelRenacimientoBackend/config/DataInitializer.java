package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.EstadoObraEnum;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.model.ObraModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.obra.repository.ObraRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.model.PermissionModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.permission.repository.PermissionRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.repository.RoleRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRespository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObraRespository obraRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            seedDataBae();
        }
    }

    private void seedDataBae() {

        System.out.println("🚧 Inicializando datos base...");

        // Crear permisos basicos
        PermissionModel create = permissionRepository.save(PermissionModel.builder()
                .name("CREATE")
                .description("Puede crear registros")
                .build());
        PermissionModel read = permissionRepository.save(PermissionModel.builder()
                .name("READ")
                .description("Puede leer registros")
                .build());
        PermissionModel update = permissionRepository.save(PermissionModel.builder()
                .name("UPDATE")
                .description("Puede actualizar registros")
                .build());
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
                .build());
        RoleModel userRole = roleRepository.save(RoleModel.builder()
                .name("ROLE_USER")
                .description("Rol de usuario con permisos limitados")
                .permissions(Set.of(create, read, update))
                .build());
        RoleModel guestRole = roleRepository.save(RoleModel.builder()
                .name("ROLE_GUEST")
                .description("Rol de invitado con permisos muy limitados")
                .permissions(Set.of(read))
                .build());

        // Crear un usuarios por defecto
        UserModel adminUser = userRepository.save(UserModel.builder()
                .name("Admin")
                .middleName("Default")
                .firstLastName("Default")
                .secondLastName("User")
                .phone("5555555555")
                .email("admin@cimientosdelrenacimiento.gob.mx")
                .password(passwordEncoder.encodePassword("Admin_2025"))
                .active(true)
                .isFirstLogin(false)
                .twoFactorEnabled(false)
                .twoFactorSecret(null)
                .verificationCode(null)
                .codeExpiration(null)
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
                .active(true)
                .isFirstLogin(false)
                .twoFactorEnabled(false)
                .twoFactorSecret(null)
                .verificationCode(null)
                .codeExpiration(null)
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
                .active(false)
                .isFirstLogin(false)
                .twoFactorEnabled(false)
                .twoFactorSecret(null)
                .verificationCode(null)
                .codeExpiration(null)
                .role(guestRole)
                .build());

        // Sembrar obras de prueba solo si no existen para evitar duplicados en cada arranque
        if (obraRepository.count() == 0) {
            seedObras(adminUser);
        }

        System.out.println("✅ Datos iniciales creados correctamente.");
    }

    private void seedObras(UserModel creator) {
        System.out.println("🏗️ Generando obras de prueba con imágenes fake...");

        String[] municipios = { "Mérida", "Progreso", "Kanasín", "Valladolid", "Tizimín", "Umán" };
        EstadoObraEnum[] estados = EstadoObraEnum.values();

        for (int i = 1; i <= 15; i++) {
            ObraModel obra = new ObraModel();
            obra.setName("Proyecto de Infraestructura #" + i);
            obra.setMunicipality(municipios[i % municipios.length]);
            obra.setAgency("Secretaría del Bienestar - Delegación " + i);
            obra.setInvestment(new BigDecimal("250000.00").multiply(new BigDecimal(i)));
            obra.setProgress(i * 6);
            obra.setDescription("Esta es una descripción de prueba para la obra número " + i
                    + ". Se enfoca en la mejora de servicios públicos.");
            obra.setLatitude(20.96737);
            obra.setLongitude(-89.59258);
            obra.setStatus(estados[i % estados.length]);

            // Asignación manual de auditoría ya que JPA Auditing no detecta usuario en el
            // arranque
            obra.setCreatedBy(creator);
            obra.setUpdatedBy(creator);

            // 5. Agregar Imágenes Fake (2 por obra para probar la relación)
            for (int j = 1; j <= 2; j++) {
                ObraImageModel img = new ObraImageModel();
                img.setUrl("https://picsum.photos/seed/" + i + j + "/800/600");
                img.setThumbUrl("https://picsum.photos/seed/" + i + j + "/200/200");
                img.setProviderId("fake-cloudflare-id-" + i + "-" + j);
                img.setMimeType("image/jpeg");
                img.setSize("150KB");
                img.setPosition(j);
                img.setCreatedBy(creator);
                img.setUpdatedBy(creator);

                // Usamos tu método helper addImage para mantener la sincronía bidireccional
                obra.addImage(img);
            }

            obraRepository.save(obra);
        }
        System.out.println("✅ 15 obras con imágenes inicializadas.");
    }
}
