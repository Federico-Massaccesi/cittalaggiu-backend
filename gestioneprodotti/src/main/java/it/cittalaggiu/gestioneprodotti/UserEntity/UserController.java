package it.cittalaggiu.gestioneprodotti.UserEntity;

import it.cittalaggiu.gestioneprodotti.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")

public class UserController {

    @Autowired
    private UserService user;


    @GetMapping
    public List<UserEntity> getAllUsersWithSpecificRoles() {
        return user.findAllWithSpecificRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getById(@PathVariable Long id) {

        return ResponseEntity.ok(user.findById(id));
    }

    @GetMapping("/search")
    public List<UserEntity> searchUsersByUsername(@RequestParam(name = "q") String query) {
        return user.searchUsersByUsername(query);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisteredUserDTO> register(@RequestBody @Validated RegisterUserModel model, BindingResult validator){
        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }
try {
    var registeredUser = user.register(
            RegisterUserDTO.builder()
                    .withUsername(model.username())
                    .withEmail(model.email())
                    .withPassword(model.password())
                    .withAddress(model.address())
                    .withTown(model.town())
                    .withCap(model.cap())
                    .withCompanyName(model.companyName())
                    .withPiva(model.piva())
                    .withRoles(model.roles())
                    .withNewsletter(model.newsletter())
                    .build());

    return  new ResponseEntity<> (registeredUser, HttpStatus.OK);
}catch (Exception e){
    e.printStackTrace();
    throw new RuntimeException("Errore durante la registrazione");
}

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated LoginModel model, BindingResult validator) {
        if (validator.hasErrors()) {
            throw  new ApiValidationException(validator.getAllErrors());
        }
        return new ResponseEntity<>(user.login(model.username(), model.password()).orElseThrow(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        user.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody @Validated UserEntity updatedUser, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(user.updateUser(id, updatedUser));
    }
}
