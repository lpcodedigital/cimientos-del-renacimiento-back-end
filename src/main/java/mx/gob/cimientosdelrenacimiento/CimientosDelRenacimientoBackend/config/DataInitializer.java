package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.jdbc.core.JdbcTemplate;

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
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CursoRepository cursoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        boolean isProd = activeProfiles.contains("prod");

        // 1. CONFIGURACIÓN AUTOMÁTICA DE FULL-TEXT SEARCH NATIVO
        log.info("🔍 Inicializando y verificando soporte Full-Text Search (FTS)...");
        setupFullTextSearch();

        // 2. SEMBRADO DE DATOS BÁSICOS: PERMISOS, ROLES, USUARIOS
        log.info("🔑 Sembrando datos de seguridad (permisos, roles y usuarios)...");
        if (roleRepository.count() == 0) {
            seedPermissionsRolesUsers();
        }

        if (municipioRepository.count() == 0) {
            seedMunicipios();
        }

        // CARGA FORZADA DE CURSOS FAKE (Fuera del if !isProd)
        // if (cursoRepository.count() == 0) {
        // log.info("🎓 Cargando cursos iniciales...");
        // userRepository.findByEmail("admin@cimientosdelrenacimiento.gob.mx")
        // .ifPresent(this::seedCursos);
        // }

        // BLOQUE DE DATOS FAKE: Solo si NO es producción
        if (!isProd) {
            log.info("🧪 Entorno de desarrollo detectado. Sembrando datos de prueba...");

            // Sembrar obras si la tabla está vacía
            if (obraRepository.count() == 0) {
                // Buscamos al admin para asignarle la autoría de los registros
                UserModel admin = userRepository.findByEmail("admin@cimientosdelrenacimiento.gob.mx")
                        .orElse(null);
                if (admin != null) {
                    seedObras(admin);
                }
            }

            // Sembrar cursos si la tabla está vacía
            if (cursoRepository.count() == 0) {
                // Buscamos al admin para asignarle la autoría de los registros
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

    private void setupFullTextSearch() {
        // Este método se puede usar para ejecutar scripts SQL que configuren el
        // search_vector
        // en PostgreSQL, creando índices GIN, etc. Se puede llamar desde el run() o
        // ejecutarse
        // manualmente después de desplegar la aplicación.

        try {

            // 1. Crear la extensión unaccent si no existe (Requiere permisos de
            // superusuario en la DB local/VPS)
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent");

            // 2. CORRECCIÓN CRÍTICA: Validar si la configuración de búsqueda ya existe en
            // el catálogo de Postgres
            Boolean configExists = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM pg_ts_config WHERE cfgname = 'es_unaccent')",
                    Boolean.class);

            // 3. Si no existe, crear la configuración personalizada basada en Spanish pero
            // con unaccent
            if (Boolean.FALSE.equals(configExists)) {
                log.info("✨ Creando configuración de búsqueda personalizada 'es_unaccent'...");
                jdbcTemplate.execute("CREATE TEXT SEARCH CONFIGURATION es_unaccent (COPY = spanish)");
                jdbcTemplate.execute(
                        "ALTER TEXT SEARCH CONFIGURATION es_unaccent ALTER MAPPING FOR hword, hword_part, word WITH unaccent, spanish_stem");
                log.info("✅ Configuración de búsqueda 'es_unaccent' creada con éxito.");
            } else {
                log.info("✅ La configuración de búsqueda 'es_unaccent' ya existe. Omitiendo creación.");
            }

            // === CONFIGURACIÓN PARA OBRAS ===
            Boolean obraColExists = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='obras' AND column_name='search_vector')",
                    Boolean.class);

            if (Boolean.FALSE.equals(obraColExists)) {
                log.info("🏗️ Agregando columna calculada search_vector a la tabla 'obras'...");
                jdbcTemplate.execute(
                        "ALTER TABLE obras ADD COLUMN search_vector tsvector " +
                                "GENERATED ALWAYS AS (" +
                                "  to_tsvector('es_unaccent', COALESCE(name, '') || ' ' || COALESCE(municipality, '') || ' ' || COALESCE(description, ''))"
                                +
                                ") STORED");

                log.info("⚡ Creando índice GIN para búsquedas sobre 'obras'...");
                jdbcTemplate.execute(
                        "CREATE INDEX IF NOT EXISTS obras_search_vector_idx ON obras USING gin(search_vector)");
                log.info("✅ Columna generada e índice GIN creados para obras.");
            } else {
                log.info("✅ La columna search_vector ya existe en obras. Omitiendo configuración.");
            }

            // === CONFIGURACIÓN PARA CURSOS ===
            // 1. La función PL/pgSQL siempre se puede reemplazar (Mantenimiento ágil sin
            // DROP)
            // === CONFIGURACIÓN PARA CURSOS ===
            log.info("🎓 Verificando/Actualizando función de búsqueda relacional para cursos...");

            // Esta funcion se puede ejecutar directamente en la BD si se hace alguna modificación, no requiere DROP ni afecta los datos existentes
            jdbcTemplate.execute(
                    "CREATE OR REPLACE FUNCTION trigger_update_cursos_search_vector() " +
                            "RETURNS trigger AS $$ " +
                            "DECLARE " +
                            "    v_municipio_name TEXT := ''; " +
                            "BEGIN " +
                            "    IF NEW.municipio_id IS NOT NULL THEN " +
                            "        SELECT COALESCE(name, '') INTO v_municipio_name " +
                            "        FROM municipios WHERE id = NEW.municipio_id; " +
                            "    END IF; " +
                            " " +
                            "    NEW.search_vector := to_tsvector('es_unaccent', " +
                            "        COALESCE(NEW.title, '') || ' ' || " +
                            "        COALESCE(NEW.description, '') || ' ' || " +
                            "        COALESCE(v_municipio_name, '') " +
                            "    ); " +
                            "    RETURN NEW; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql;" // Así, pegado al END; anterior sin saltos de línea intermedios
            );

            // 2. La columna, el índice y el Trigger solo se configuran si la columna no
            // existe
            Boolean cursosColExist = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='cursos' AND column_name='search_vector')",
                    Boolean.class);

            if (Boolean.FALSE.equals(cursosColExist)) {
                log.info("🎓 Creando columna física, índice GIN y Trigger para la tabla 'cursos'...");

                jdbcTemplate.execute("ALTER TABLE cursos ADD COLUMN search_vector tsvector");
                jdbcTemplate.execute("CREATE INDEX idx_cursos_search_vector ON cursos USING GIN(search_vector)");

                // Atamos el disparador a la tabla
                jdbcTemplate.execute(
                        "CREATE TRIGGER trg_cursos_search_vector " +
                                "BEFORE INSERT OR UPDATE ON cursos " +
                                "FOR EACH ROW EXECUTE FUNCTION trigger_update_cursos_search_vector();");

                // Forzamos un update rápido para calcular el vector en los datos que se
                // siembren en el arranque
                // Con este query despus de ejecutar la función en la BD que actualiza el search_vector se puede ejutar esto en BD para forzar a despertar el trigger y asi aplicar 
                // la nueva logica en los datos viejos
                jdbcTemplate.execute("UPDATE cursos SET title = title;");
            }

            log.info("✅ Arquitectura mixta Full-Text Search (Obras + Cursos) lista.");

        } catch (Exception e) {
            log.error("❌ Error al configurar búsqueda de texto completo: {}", e.getMessage());
            e.printStackTrace();
        }

    }

    private void seedPermissionsRolesUsers() {

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
        userRepository.save(UserModel.builder()
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

    public void seedMunicipios() {
        try {
            log.info("🌐 Sembrando municipios desde municipios.json...");

            // 1. Cargamos el archivo (asegúrate que esté en src/main/resources/)
            JsonNode root = objectMapper.readTree(new ClassPathResource("municipios.json").getInputStream());

            // 2. IMPORTANTE: Accedemos al nodo "municipios" que contiene el array
            // Si haces root.isArray() dará falso porque root es un objeto { "municipios":
            // [...] }
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
        log.info("🎓 Sembrando cursos de capacitación de prueba...");

        List<MunicipioModel> todosLosMunicipios = municipioRepository.findAll();
        if (todosLosMunicipios.isEmpty()) {
            log.warn("⚠️ No hay municipios para asignar a los cursos. Saltando seed.");
            return;
        }

        String[] temas = {
                "Taller de Programación Web",
                "Carpintería Básica",
                "Introducción a la Electrónica",
                "Sostenibilidad Ambiental",
                "Gestión de Proyectos Comunitarios",
                "Primeros Auxilios",
                "Corte y Confección"
        };

        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            CursoModel curso = new CursoModel();
            curso.setTitle(temas[i % temas.length] + " - Grupo " + (i + 1));
            curso.setDescription(
                    "Este es un curso de prueba diseñado para fortalecer las habilidades técnicas de los ciudadanos en el estado de Yucatán. Sesión #"
                            + i);
            curso.setCourseDate(LocalDate.now().plusDays(random.nextInt(30)));

            // Asignar un municipio aleatorio de la lista ya cargada
            curso.setMunicipality(todosLosMunicipios.get(random.nextInt(todosLosMunicipios.size())));

            curso.setCreatedBy(creator);
            curso.setUpdatedBy(creator);

            // Agregar 3 imágenes por curso (la primera será la portada automáticamente por
            // posición 0)
            for (int j = 0; j < 3; j++) {
                CursoImageModel img = new CursoImageModel();
                // Usamos una semilla diferente para cada imagen para que no sean iguales
                String seed = "curso-" + i + "-" + j;
                img.setUrl("https://picsum.photos/seed/" + seed + "/800/600");
                img.setThumbUrl("https://picsum.photos/seed/" + seed + "/300/300");
                img.setProviderId("fake-r2-" + seed);
                img.setMimeType("image/jpeg");
                img.setSize("120KB");
                img.setPosition(j);
                img.setCreatedBy(creator);
                img.setUpdatedBy(creator);

                // Sincronización bidireccional
                curso.addImage(img);

                // ASIGNACIÓN DE PORTADA: Si es la posición 0, la marcamos como coverImage
                if (j == 0) {
                    curso.setCoverImage(img);
                }
            }

            cursoRepository.save(curso);
        }
        log.info("✅ 12 cursos inicializados correctamente.");
    }
}
