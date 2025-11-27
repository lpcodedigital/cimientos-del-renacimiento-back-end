package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.service;

import java.time.LocalDateTime;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.model.RoleModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.role.repository.RoleRepository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.dto.UserDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.dto.UserRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.mapper.UserMapper;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.model.UserModel;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.repository.UserRespository;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.util.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRespository userRespository;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    public Page<UserDTO> getAllUsers(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        //Page<UserModel> userPage = userRespository.findAllActive(pageable);
        Page<UserModel> userPage = userRespository.findAll(pageable);
            
        return userPage.map(userMapper::toUserDTO);
    }

    public Optional<UserDTO> getUserById(Long id){
        return userRespository.findById(id).map(userMapper::toUserDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRespository.findByEmail(email).map(userMapper::toUserDTO);
    }

    public UserDTO create(UserRequestDTO userRequestDTO) throws Exception {

        // Checar si el correo ya está registrado
        if( userRespository.findByEmail(userRequestDTO.getEmail()).isPresent() ) {
            throw new Exception("El correo electrónico ya está registrado");
        }

        // Validar que el rol se haya proporcionado
        if( roleRepository.findById(userRequestDTO.getRoleId()).isEmpty() ) {
            // Se asigna el rol por defecto "ROLE_VIWER"
           RoleModel defaultRole = roleRepository.findByName("ROLE_GUEST")
            .orElseThrow(() -> new Exception("Rol por defecto no encontrado: ROLE_GUEST"));
            userRequestDTO.setRoleId(defaultRole.getIdRole());
        }

        // Obtener el rol por ID
        RoleModel roleModel = roleRepository.findById(userRequestDTO.getRoleId())
            .orElseThrow(() -> new Exception("Rol no encontrado por ID: " + userRequestDTO.getRoleId()));

        // Mapear DTO a modelo
        UserModel newUser = userMapper.toUserModel(userRequestDTO, roleModel);

        // Encriptar la contraseña y asignarla al usuario
        newUser.setPassword( passwordEncoder.encodePassword(userRequestDTO.getPassword()) );

        // Asignar el rol al usuario
        //newUser.setRole(roleModel);

        // Guardar el usuario en la base de datos
        UserModel savedUser = userRespository.save(newUser);

        // Mapear modelo a DTO
        return userMapper.toUserDTO(savedUser);
    }

    public Optional<UserDTO> updateUser(Long id, UserRequestDTO userRequestDTO ) throws Exception {

        UserModel user = userRespository.findById(id)
            .orElseThrow(() -> new RuntimeException("El usuario con ID: " + id + " no fue encontrado."));

        if (user.isDeleted()) {
            throw new RuntimeException("El usuario con ID: " + id + " no puede ser actualizado porque ya ha sido eliminado.");
            
        }

        RoleModel role = null;
        if (userRequestDTO.getRoleId() != null){
            role  = roleRepository.findById(userRequestDTO.getRoleId())
                .orElseThrow( () -> new RuntimeException("Rol no encontrado con ID: " + userRequestDTO.getRoleId()) );
        }

        userMapper.updateUserFromDTO(userRequestDTO, user, role);

        UserModel updatedUser = userRespository.save(user);

        return Optional.ofNullable(userMapper.toUserDTO(updatedUser));

        
    }


    
    public Boolean deleteUserById(Long id) {
        if(!userRespository.existsById(id)) {
            throw new RuntimeException("El usuario con ID: " + id + " no fue encontrado.");
        }

        userRespository.findById(id).ifPresent(user -> {
            user.setDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            userRespository.save(user);
            
        });

        return true;
    }
}
