package it.cittalaggiu.gestioneprodotti.security;

import it.cittalaggiu.gestioneprodotti.UserEntity.Roles;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RegisteredUserDTO {
    Long id;
    String username;
    private List<Roles> roles;


    @Builder(setterPrefix = "with")
    public RegisteredUserDTO(Long id, String username, List<Roles> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}