package it.cittalaggiu.gestioneprodotti.guest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;


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

    @Builder.Default
    private Double debt = 0.00;

    @ManyToOne
    @JsonIgnore
    private Association association;

}
