package it.cittalaggiu.gestioneprodotti.guestFee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.guest.Guest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Table(name = "guests_fee")
public class GuestsFee extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "association_id", nullable = false) // Specifica la colonna association_id
    private Association association;

    @ManyToMany
    @JoinTable(
            name = "guest_fees", // Nome della tabella di join
            joinColumns = @JoinColumn(name = "guest_fee_id"), // Colonna per GuestsFee
            inverseJoinColumns = @JoinColumn(name = "guest_id") // Colonna per Guest
    )
    private List<Guest> guestsFee;

    private LocalDate month;

    private double amount;
}
