package it.cittalaggiu.gestioneprodotti.association;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.UserEntity.UserEntity;
import it.cittalaggiu.gestioneprodotti.expense.Expense;
import it.cittalaggiu.gestioneprodotti.guest.Guest;
import it.cittalaggiu.gestioneprodotti.products.Product;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guest> guests;

    @Builder.Default
    private double totalExpenses = 0;

    @ElementCollection
    @CollectionTable(name = "association_images", joinColumns = @JoinColumn(name = "association_id"))
    @Column(name = "image_url")
    private List<String> imagesUrl;

    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToOne
    @JoinColumn(name = "admin_id")
    private UserEntity admin;

    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}
