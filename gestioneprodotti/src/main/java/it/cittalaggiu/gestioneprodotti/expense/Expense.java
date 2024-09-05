package it.cittalaggiu.gestioneprodotti.expense;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.products.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "expenses")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Expense extends BaseEntity {

    private List<Product> products;
    private double totalPrice = calculateTotalPrice();

    @ManyToOne
    private Association association;

    private double calculateTotalPrice() {
        assert products != null;
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
