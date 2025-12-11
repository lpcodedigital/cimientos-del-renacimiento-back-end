package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.service;

import java.time.LocalDateTime;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ConflictException;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.exception.ResourceNotFoundException;
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

    public UserDTO getUserByEmail(String email) {
        var user = userRespository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con email: " + email + " no encontrado."));
        return userMapper.toUserDTO(user);
    }

    public UserDTO create(UserRequestDTO userRequestDTO) {

        // Checar si el correo ya está registrado unicamente entre los usuarios activos excluyendo los eliminados por soft delete
        //if( userRespository.findByEmail(userRequestDTO.getEmail()).isPresent() ) {
        //    throw new ConflictException("El correo electrónico ya está registrado");
        //}

        // Checar si el correo ya está registrado, sin importar el estado activo/inactivo incluyendo los eliminados por soft delete
        if (userRespository.findAnyByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ConflictException("El correo electrónico ya está registrado");
        }


        // Validar que el rol se haya proporcionado
        if( roleRepository.findById(userRequestDTO.getRoleId()).isEmpty() ) {
            // Se asigna el rol por defecto "ROLE_VIWER"
           RoleModel defaultRole = roleRepository.findByName("ROLE_GUEST")
            .orElseThrow(() -> new ResourceNotFoundException(" El Rol por defecto GUEST no encontrado"));
            userRequestDTO.setRoleId(defaultRole.getIdRole());
        }

        // Obtener el rol por ID
        RoleModel roleModel = roleRepository.findById(userRequestDTO.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("El Rol con ID: " + userRequestDTO.getRoleId() + " no fue encontrado."));

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

    public UserDTO updateUser(Long id, UserRequestDTO userRequestDTO ) {

        UserModel user = userRespository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID: " + id + " no fue encontrado."));

        if (user.isDeleted()) {
            throw new ConflictException("El usuario con ID: " + id + " no puede ser actualizado porque ya ha sido eliminado.");
            
        }

        RoleModel role = null;
        if (userRequestDTO.getRoleId() != null){
            role  = roleRepository.findById(userRequestDTO.getRoleId())
                .orElseThrow( () -> new ResourceNotFoundException("El Rol con ID: " + userRequestDTO.getRoleId() + " no fue encontrado.") );
        }

        userMapper.updateUserFromDTO(userRequestDTO, user, role);

        UserModel updatedUser = userRespository.save(user);

        return userMapper.toUserDTO(updatedUser);

        
    }
    
    public void deleteUserById(Long id) {
        UserModel user = userRespository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID: " + id + " no fue encontrado."));

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());

        userRespository.save(user);
    
    }
}
