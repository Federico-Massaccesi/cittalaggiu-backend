package it.cittalaggiu.gestioneprodotti.UserEntity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Roles {
    public static final String ROLES_ADMIN = "ADMIN";
    public static final String ROLES_PRIVATE = "GUEST";

    @Id
    private String roleType;
}