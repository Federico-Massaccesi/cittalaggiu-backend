package it.cittalaggiu.gestioneprodotti.guest;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "guests")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Guest extends BaseEntity {

    private String name;

    private String imageUrl;

    private Double debt = 0.00;

    @ManyToOne
    private Association association;
}
