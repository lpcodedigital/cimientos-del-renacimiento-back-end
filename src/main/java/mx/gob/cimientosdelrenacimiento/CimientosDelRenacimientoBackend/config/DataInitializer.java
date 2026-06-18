package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoImageModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.model.CursoModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.curso.repository.CursoRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.model.MunicipioModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.municipio.repository.MunicipioRepository;
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
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private org.springframework.core.env.Environment environment;
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
    @Autowired
    private MunicipioRepository municipioRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CursoRepository cursoRepository;

    // 💡 Diccionario estático de coordenadas reales del centro de municipios clave en Yucatán
    // Esto garantiza que el marcador caiga exactamente dentro del municipio correspondiente
    private static final Map<String, double[]> COORDENADAS_MUNICIPIOS = Map.of(
        "Mérida", new double[]{20.96737, -89.59258},
        "Progreso", new double[]{21.28277, -89.66361},
        "Kanasín", new double[]{20.93333, -89.55833},
        "Valladolid", new double[]{20.68944, -88.20138},
        "Tizimín", new double[]{21.14222, -88.14944},
        "Umán", new double[]{20.88333, -89.75000},
        "Motul", new double[]{21.09500, -89.28388},
        "Izamal", new double[]{20.93111, -88.84972},
        "Oxkutzcab", new double[]{20.30416, -89.41777},
        "Tekax", new double[]{20.20166, -89.28444}
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        boolean isProd = activeProfiles.contains("prod");

        if (roleRepository.count() == 0) {
            seedPermissionsRolesUsers();
        }
        
        if (municipioRepository.count() == 0) {
            seedMunicipios();
        }

        if (!isProd) {
            log.info("🧪 Entorno de desarrollo detectado. Sembrando datos de prueba...");
            
            if (obraRepository.count() == 0) {
                UserModel admin = userRepository.findByEmail("admin@cimientosdelrenacimiento.gob.mx")
                        .orElse(null);
                if (admin != null) {
                    seedObras(admin);
                }
            }
           
            if (cursoRepository.count() == 0) {
                UserModel admin = userRepository.findByEmail("admin@cimientosdelrenacimiento.gob.mx")
                        .orElse(null);
                if (admin != null) {
                    seedCursos(admin);
                }
            }
        } else {
            log.info("🚀 Entorno de PRODUCCIÓN detectado. Saltando datos de prueba.");
        }
    }

    private void seedPermissionsRolesUsers() {
        System.out.println("🚧 Inicializando datos base...");

        PermissionModel create = permissionRepository.save(PermissionModel.builder().name("CREATE").description("Puede crear registros").build());
        PermissionModel read = permissionRepository.save(PermissionModel.builder().name("READ").description("Puede leer registros").build());
        PermissionModel update = permissionRepository.save(PermissionModel.builder().name("UPDATE").description("Puede actualizar registros").build());
        PermissionModel delete = permissionRepository.save(PermissionModel.builder().name("DELETE").description("Puede eliminar registros").build());

        RoleModel adminRole = roleRepository.save(RoleModel.builder().name("ROLE_ADMIN").description("Rol de administrador con todos los permisos").permissions(Set.of(create, read, update, delete)).build());
        RoleModel userRole = roleRepository.save(RoleModel.builder().name("ROLE_USER").description("Rol de usuario con permisos limitados").permissions(Set.of(create, read, update)).build());
        RoleModel guestRole = roleRepository.save(RoleModel.builder().name("ROLE_GUEST").description("Rol de invitado con permisos muy limitados").permissions(Set.of(read)).build());

        userRepository.save(UserModel.builder().name("Admin").middleName("Default").firstLastName("Default").secondLastName("User").phone("5555555555").email("admin@cimientosdelrenacimiento.gob.mx").password(passwordEncoder.encodePassword("Admin_2025")).active(true).isFirstLogin(false).twoFactorEnabled(false).role(adminRole).build());
        userRepository.save(UserModel.builder().name("User").middleName("Default").firstLastName("Default").secondLastName("User").phone("5555555551").email("user@cimientosdelrenacimiento.gob.mx").password(passwordEncoder.encodePassword("User_2025")).active(true).isFirstLogin(false).twoFactorEnabled(false).role(userRole).build());
        userRepository.save(UserModel.builder().name("Guest").middleName("Default").firstLastName("Default").secondLastName("Guest").phone("5555555552").email("guest@cimientosdelrenacimiento.gob.mx").password(passwordEncoder.encodePassword("Guest_2025")).active(false).isFirstLogin(false).twoFactorEnabled(false).role(guestRole).build());

        System.out.println("✅ Datos iniciales creados correctamente.");
    }

    private void seedObras(UserModel creator) {
        System.out.println("🏗️ Generando obras de prueba con campo locality...");

        String[] municipios = { "Mérida", "Progreso", "Kanasín", "Valladolid", "Tizimín", "Umán" };
        
        // 💡 Diccionario correlativo de localidades ficticias/comunes asociadas a los municipios anteriores
        String[] localidades = { "Centro", "Chicxulub Puerto", "San Haroldo", "Nachi Cocom", "El Palmar", "Itzincab" };
        
        EstadoObraEnum[] estados = EstadoObraEnum.values();

        for (int i = 1; i <= 15; i++) {
            int index = i % municipios.length;
            String nombreMunicipio = municipios[index];
            
            ObraModel obra = new ObraModel();
            obra.setName("Proyecto de Infraestructura #" + i);
            obra.setMunicipality(nombreMunicipio);
            
            // 💡 ASIGNACIÓN DEL NUEVO CAMPO LOCALITY CORRESPONDIENTE
            obra.setLocality(localidades[index]);
            
            obra.setAgency("Secretaría del Bienestar - Delegación " + i);
            obra.setInvestment(new BigDecimal("250000.00").multiply(new BigDecimal(i)));
            obra.setProgress(i * 6);
            obra.setDescription("Esta es una descripción de prueba para la obra número " + i + ". Se enfoca en la mejora de servicios públicos.");
            
            // 💡 Alineamos también las coordenadas de las obras con su municipio para evitar desajustes en el mapa
            double[] coords = COORDENADAS_MUNICIPIOS.getOrDefault(nombreMunicipio, new double[]{20.96737, -89.59258});
            obra.setLatitude(coords[0] + (i * 0.002)); // Pequeña dispersión para que no se encimen exactamente
            obra.setLongitude(coords[1] + (i * 0.002));
            
            obra.setStatus(estados[i % estados.length]);

            obra.setCreatedBy(creator);
            obra.setUpdatedBy(creator);

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

                obra.addImage(img);
            }

            obraRepository.save(obra);
        }
        System.out.println("✅ 15 obras inicializadas con localidades mapeadas.");
    }

    public void seedMunicipios() {
        try {
            log.info("🌐 Sembrando municipios desde municipios.json...");
            JsonNode root = objectMapper.readTree(new ClassPathResource("municipios.json").getInputStream());
            JsonNode municipiosArray = root.path("municipios");

            if (municipiosArray.isArray()) {
                for (JsonNode node : municipiosArray) {
                    MunicipioModel municipio = MunicipioModel.builder()
                            .name(node.path("nomgeo").asText())
                            .cveGeo(node.path("cvegeo").asText())
                            .cveEnt(node.path("cve_ent").asText())
                            .cveMun(node.path("cve_mun").asText())
                            .cveCab(node.path("cve_cab").asText())
                            .pobMasculina(node.path("pob_masculina").asText())
                            .pobFemenina(node.path("pob_femenina").asText())
                            .pobTotal(node.path("pob_total").asText())
                            .totalViviendas(node.path("total_viviendas_habitadas").asText())
                            .build();

                    municipioRepository.save(municipio);
                }
                log.info(" ✅ Carga completada: {} municipios insertados.", municipioRepository.count());
            } else {
                log.error(" ❌ No se encontró el array 'municipios' dentro del archivo JSON.");
            }
        } catch (Exception e) {
            log.error(" ❌ Error crítico al cargar municipios: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedCursos(UserModel creator) {
        log.info("🎓 Sembrando cursos alineados geográficamente con su municipio...");

        List<MunicipioModel> todosLosMunicipios = municipioRepository.findAll();
        if (todosLosMunicipios.isEmpty()) {
            log.warn("⚠️ No hay municipios en la base de datos. Saltando seed.");
            return;
        }

        String[] temas = {
                "Taller de Programación Web", "Carpintería Básica", "Introducción a la Electrónica",
                "Sostenibilidad Ambiental", "Gestión de Proyectos Comunitarios", "Primeros Auxilios",
                "Corte y Confección"
        };

        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            CursoModel curso = new CursoModel();
            curso.setTitle(temas[i % temas.length] + " - Grupo " + (i + 1));
            curso.setDescription("Este es un curso de prueba diseñado para fortalecer las habilidades técnicas de los ciudadanos en el estado de Yucatán. Sesión #" + i);
            curso.setCourseDate(LocalDate.now().plusDays(random.nextInt(30)));

            // 💡 ASIGNACIÓN ALEATORIA DEL MUNICIPIO DESDE LA BASE DE DATOS
            MunicipioModel municipioAsignado = todosLosMunicipios.get(random.nextInt(todosLosMunicipios.size()));
            curso.setMunicipality(municipioAsignado);

            // 💡 CORRELACIÓN GEOGRÁFICA STRICTA:
            // Buscamos si el nombre del municipio asignado tiene coordenadas cargadas en nuestro diccionario de control.
            // Si no está listado en el Map, usamos el centro de Mérida como respaldo seguro.
            double[] coordsBase = COORDENADAS_MUNICIPIOS.get(municipioAsignado.getName());
            if (coordsBase == null) {
                // Si es un municipio del interior no listado, forzamos uno conocido para asegurar coincidencia visual en las capas
                municipioAsignado = todosLosMunicipios.stream()
                        .filter(m -> COORDENADAS_MUNICIPIOS.containsKey(m.getName()))
                        .findFirst()
                        .orElse(municipioAsignado);
                curso.setMunicipality(municipioAsignado);
                coordsBase = COORDENADAS_MUNICIPIOS.get(municipioAsignado.getName());
            }

            // Aplicamos una microdispersión matemática muy leve (rango de 500 metros) 
            // para que si se generan dos cursos en el mismo municipio, no queden exactamente encimados.
            double dispersionLat = (random.nextDouble() - 0.5) * 0.009;
            double dispersionLng = (random.nextDouble() - 0.5) * 0.009;
            
            curso.setLatitude(coordsBase[0] + dispersionLat);
            curso.setLongitude(coordsBase[1] + dispersionLng);

            curso.setCreatedBy(creator);
            curso.setUpdatedBy(creator);

            for (int j = 0; j < 3; j++) {
                CursoImageModel img = new CursoImageModel();
                String seed = "curso-" + i + "-" + j;
                img.setUrl("https://picsum.photos/seed/" + seed + "/800/600");
                img.setThumbUrl("https://picsum.photos/seed/" + seed + "/300/300");
                img.setProviderId("fake-r2-" + seed);
                img.setMimeType("image/jpeg");
                img.setSize("120KB");
                img.setPosition(j);
                img.setCreatedBy(creator);
                img.setUpdatedBy(creator);

                curso.addImage(img);

                if (j == 0) {
                    curso.setCoverImage(img);
                }
            }

            cursoRepository.save(curso);
        }
        log.info("✅ 12 cursos inicializados y georreferenciados correctamente con sus municipios.");
    }
}