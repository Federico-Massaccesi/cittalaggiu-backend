package it.cittalaggiu.gestioneprodotti.UserEntity;

import it.cittalaggiu.gestioneprodotti.security.JwtUtils;
import it.cittalaggiu.gestioneprodotti.security.LoginResponseDTO;
import it.cittalaggiu.gestioneprodotti.security.RegisterUserDTO;
import it.cittalaggiu.gestioneprodotti.security.RegisteredUserDTO;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    AuthenticationManager auth;
    @Autowired
    JwtUtils jwt;


    public Optional<LoginResponseDTO> login(String username, String password) {
        try {

            var a = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            a.getAuthorities();

            SecurityContextHolder.getContext().setAuthentication(a);

            var user = userRepository.findOneByUsername(username).orElseThrow();
            var dto = LoginResponseDTO.builder()
                    .withUser(RegisteredUserDTO.builder()
                            .withId(user.getId())
                            .withRoles(user.getRoles())
                            .withUsername(user.getUsername())
                            .build())
                    .build();


            dto.setToken(jwt.generateToken(a));

            return Optional.of(dto);
        } catch ( AuthenticationException e) {
            throw new RuntimeException(username);
        }
    }

    public RegisteredUserDTO register(RegisterUserDTO register){
        if(userRepository.existsByUsername(register.getUsername())) {
            throw new EntityExistsException("Utente gia' esistente");
        }
        Roles roles = rolesRepository.findById(register.getRoles().get(0).getRoleType())
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non valido"));

        UserEntity u = new UserEntity();
        BeanUtils.copyProperties(register, u);

        u.setPassword(encoder.encode(register.getPassword()));
        u.setPin(encoder.encode(register.getPin()));
        u.getRoles().add(roles);
        userRepository.save(u);

        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));
        return response;

    }


    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }



    public UserEntity findById(Long id){
        var founded = userRepository.findById(id);
        if(founded.isPresent()){
            return userRepository.findById(id).get();
        }else{
            throw new RuntimeException("Utente non trovato");
        }
    }
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + id));

        BeanUtils.copyProperties(updatedUser, existingUser, "id", "password", "roles");

        return userRepository.save(existingUser);
    }
    public void deleteById(Long id){
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato con id: " + id);
        }
        userRepository.deleteById(id);    }



    public List<UserEntity> searchUsersByUsername(String prefix) {
        return userRepository.findByUsernameStartingWithIgnoreCase(prefix);
    }

//    public UserEntity addToDebt(Long id, Integer amount) {
//        UserEntity existingUser = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + id));
//        existingUser.setDebt(existingUser.getDebt() + amount);
//        return userRepository.save(existingUser);
//    }
//
//    public UserEntity resetDebt(Long id) {
//        UserEntity existingUser = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + id));
//        existingUser.setDebt(0);
//        return userRepository.save(existingUser);
//    }
}
