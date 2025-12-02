package mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.dto.UserDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.dto.UserRequestDTO;
import mx.gob.cimientosdelrenacimiento.CimientosDelRenacimientoBackend.user.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size) {
        Page<UserDTO> users = userService.getAllUsers(page, size);

       Map<String, Object> response = new HashMap<>();

       response.put("users", users.getContent());
       response.put("currentPage", users.getNumber());
       response.put("totalItems", users.getTotalElements());
       response.put("totalPages", users.getTotalPages());
       response.put("pageSize", users.getSize());


       return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) throws Exception {
        UserDTO registeredUser = userService.create(userRequestDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update/{id}")
    ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO userRequestDTO) throws Exception {
        
       return userService.updateUser(id, userRequestDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
        
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deleteUser(@PathVariable Long id){

        Map<String, String> response = new HashMap<>();
        
        boolean deleted = userService.deleteUserById(id);
        if(!deleted) {
            response.put("message","Usuario con ID " + id + " no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }   
        response.put("message","Usuario con ID " + id + " eliminado correctamente.");
        return ResponseEntity.ok(response);
    }

}
