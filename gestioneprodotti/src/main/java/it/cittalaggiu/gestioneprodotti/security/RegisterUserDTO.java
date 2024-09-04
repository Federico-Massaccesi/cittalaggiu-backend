package it.cittalaggiu.gestioneprodotti.security;


import it.cittalaggiu.gestioneprodotti.UserEntity.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class RegisterUserDTO {
    String username;
    String email;
    String password;
    String companyName;
    String piva;
    String address;
    String town;
    List<Roles> roles;
    Boolean newsletter;
    Long telephoneNumber;
    Long cap;

}