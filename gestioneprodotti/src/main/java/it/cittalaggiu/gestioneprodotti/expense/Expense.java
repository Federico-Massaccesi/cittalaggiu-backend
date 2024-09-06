package it.cittalaggiu.gestioneprodotti.expense;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.products.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "expenses")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Expense extends BaseEntity {

    @ManyToMany
    @JoinTable(
            name = "expense_products",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    private LocalDate date;

    public double getTotalPrice() {
        if (products == null) {
            return 0;
        }
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
