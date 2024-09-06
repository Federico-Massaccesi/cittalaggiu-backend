package it.cittalaggiu.gestioneprodotti.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.expense.Expense;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
    @Data
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(setterPrefix = "with")
    @Table(name = "products")
    public class Product extends BaseEntity {

        private String name;

        private Double price;

        private String imageURL;

        private Boolean available;

        private Integer quantity = 0;

        @ManyToOne
        @JoinColumn(name = "association_id")
        @JsonIgnore
        private Association association;

        @ManyToMany(mappedBy = "products")
        private List<Expense> expenses;
}
