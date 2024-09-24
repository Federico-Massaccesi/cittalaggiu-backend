package it.cittalaggiu.gestioneprodotti.UserEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    private String username;

    private String password;

    private String pin;

    @ManyToMany(fetch = FetchType.EAGER)
    private final List<Roles> roles = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Association> associations = new ArrayList<>();
}
