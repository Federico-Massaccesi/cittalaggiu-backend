package it.cittalaggiu.gestioneprodotti.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ProductValidPost(
        @NotBlank
         String name,
        @NotNull
         Double price,
        @NotNull
        Boolean available
) {
}
