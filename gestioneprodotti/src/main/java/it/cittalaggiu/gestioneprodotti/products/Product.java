package it.cittalaggiu.gestioneprodotti.products;

import it.cittalaggiu.gestioneprodotti.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


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
}
