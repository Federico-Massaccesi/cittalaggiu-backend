package it.cittalaggiu.gestioneprodotti.association;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.UserEntity.UserEntity;
import it.cittalaggiu.gestioneprodotti.expense.Expense;
import it.cittalaggiu.gestioneprodotti.guest.Guest;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "associations")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Association extends BaseEntity {

    private String name;

    @OneToMany
    private List<Guest> guests;

    private double totalExpenses;

    private List<String> imagesUrl;

    @OneToMany(mappedBy = "association")
    private List<Expense> expenses;

    private UserEntity admin;
}
