package it.cittalaggiu.gestioneprodotti.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @PostMapping
    public ResponseEntity<Roles> createRole(@RequestBody Roles role) {
        if (rolesService.existsByRoleType(role.getRoleType())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Roles createdRole = rolesService.create(role);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }


}

